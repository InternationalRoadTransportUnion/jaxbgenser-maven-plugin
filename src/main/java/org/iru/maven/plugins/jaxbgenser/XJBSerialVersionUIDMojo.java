package org.iru.maven.plugins.jaxbgenser;

import java.io.File;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;


/**
 * 
 * @phase generate-sources
 * @goal generate-xjb
 *
 */
public class XJBSerialVersionUIDMojo extends AbstractMojo {

	/**
	 * @parameter default-value="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * The directory where to create the bindings files
	 *
	 * @parameter default-value="${basedir}/target/xjb"
	 */
	private File bindingDirectory;

	/**
	 * The globalBindings file
	 * 
	 * @parameter default-value="globalBindings.xjb"
	 */
	private String globalBindings;

	private Namespace JAXB_NAMESPACE = new Namespace("jaxb", "http://java.sun.com/xml/ns/jaxb");

	static String extractMajorAndMinor(String version) {
		Pattern versionPattern = Pattern.compile("^((\\d+)(\\.\\d+)?)\\.?.*");
		Matcher m = versionPattern.matcher(version); 
		if (m.matches())
			return m.group(1);
		else
			return version;
	}
	
	public void execute() throws MojoExecutionException, MojoFailureException {

		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-1");
			StringBuilder sb = new StringBuilder(project.getGroupId());
			sb.append(":").append(project.getArtifactId());
			sb.append(":").append(extractMajorAndMinor(project.getVersion()));
			sb.append(":").append(project.getPackaging());
			md.update(sb.toString().getBytes("UTF-8"));
			byte[] b = md.digest();
			long l = 0;
			for (int i = 0; i < 8 ; i++) {
				if (i != 0)
					l <<= 8L;	
				l += (long) b[i];
			}

			if (getLog().isInfoEnabled())
				getLog().info("Using "+sb+" to generate serialVersionUID: "+l);

			DocumentFactory docf = new DocumentFactory();
			Document doc = docf.createDocument();

			Element root = docf.createElement(new QName("bindings", JAXB_NAMESPACE));
			root.addAttribute(new QName("version", JAXB_NAMESPACE), "2.1");

			doc.setRootElement(root);
			Element gb = docf.createElement(new QName("globalBindings", JAXB_NAMESPACE));
			root.add(gb);
			Element serializable = docf.createElement(new QName("serializable", JAXB_NAMESPACE));
			serializable.addAttribute("uid", Long.toString(l));
			gb.add(serializable);


			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setIndentSize(4); 

			bindingDirectory.mkdirs();
			int i = globalBindings.lastIndexOf('.');
			String prefix = i == -1 ? globalBindings : globalBindings.substring(0, i);
			String suffix = i == -1 || i == globalBindings.length() -1 ? "tmp" : globalBindings.substring(i+1);
			File tmp = FileUtils.createTempFile(prefix, suffix, bindingDirectory);
			XMLWriter writer = new XMLWriter(new FileOutputStream(tmp), format);
			writer.write(doc);
			writer.close();
			File xjb = new File(bindingDirectory, globalBindings);
			if (FileUtils.contentEquals(xjb, tmp)) {
				tmp.delete();
			} else {
				xjb.delete();
				tmp.renameTo(xjb);
			}
		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
}
