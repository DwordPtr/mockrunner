package com.mockrunner.example.servlet;

import com.mockrunner.servlet.ServletTestCaseAdapter;

/**
 * Example test for {@link LogoutServlet}.
 * Demonstrates the usage of {@link com.mockrunner.servlet.ServletTestModule} 
 * resp. {@link com.mockrunner.servlet.ServletTestCaseAdapter}.
 */
public class LogoutServletTest extends ServletTestCaseAdapter
{
    protected void setUp() throws Exception
    {
        super.setUp();
        createServlet(LogoutServlet.class);
    }
    
    public void testDoNoLogout() throws Exception
    {
        getMockObjectFactory().getMockRequest().setupAddParameter("logout", "false");
        doPost();
        assertTrue(getMockObjectFactory().getMockSession().isValid());
    }
    
    public void testDoLogout() throws Exception
    {
        getMockObjectFactory().getMockRequest().setupAddParameter("logout", "true");
        doPost();
        assertFalse(getMockObjectFactory().getMockSession().isValid());
    }
}
