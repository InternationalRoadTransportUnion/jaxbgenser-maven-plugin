package org.iru.maven.plugins.jaxbgenser;

import junit.framework.Assert;

import org.iru.maven.plugins.jaxbgenser.XJBSerialVersionUIDMojo;
import org.junit.Test;


public class Version {

	@Test
	public void testMajorSNAPSHOT() {
		Assert.assertEquals("1", XJBSerialVersionUIDMojo.extractMajorAndMinor("1-SNAPSHOT"));
	}

	@Test
	public void testMajor() {
		Assert.assertEquals("1", XJBSerialVersionUIDMojo.extractMajorAndMinor("1"));
	}

	@Test
	public void testMajorAndMinorSNAPSHOT() {
		Assert.assertEquals("1.0", XJBSerialVersionUIDMojo.extractMajorAndMinor("1.0-SNAPSHOT"));
	}

	@Test
	public void testMajorAndMinor() {
		Assert.assertEquals("1.0", XJBSerialVersionUIDMojo.extractMajorAndMinor("1.0"));
	}

	@Test
	public void testMajorAndMinorAndMoreWithSNAPSHOT() {
		Assert.assertEquals("1.0", XJBSerialVersionUIDMojo.extractMajorAndMinor("1.0.0-SNAPSHOT"));
	}

	@Test
	public void testMajorAndMinorAndMore() {
		Assert.assertEquals("1.0", XJBSerialVersionUIDMojo.extractMajorAndMinor("1.0.0"));
	}

}
