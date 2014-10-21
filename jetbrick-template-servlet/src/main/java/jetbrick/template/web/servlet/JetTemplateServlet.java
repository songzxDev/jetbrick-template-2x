/**
 * Copyright 2013-2014 Guoqiang Chen, Shanghai, China. All rights reserved.
 *
 *   Author: Guoqiang Chen
 *    Email: subchen@gmail.com
 *   WebURL: https://github.com/subchen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrick.template.web.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import jetbrick.io.resource.ResourceNotFoundException;
import jetbrick.template.*;
import jetbrick.template.web.JetWebContext;
import jetbrick.template.web.JetWebEngine;
import jetbrick.web.servlet.RequestUtils;

/**
 * 直接作为 Servlet 使用。需要在 web.xml 中作如下配置。
 * <pre><xmp>
 * <servlet>
 *   <servlet-name>jetbrick-template</servlet-name>
 *   <servlet-class>jetbrick.template.web.servlet.JetTemplateServlet</servlet-class>
 *   <load-on-startup>1</load-on-startup>
 * </servlet>
 * <servlet-mapping>
 *   <servlet-name>jetbrick-template</servlet-name>
 *   <url-pattern>*.jetx</url-pattern>
 * </servlet-mapping>
 * </xmp></pre>
 */
public final class JetTemplateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private JetEngine engine;
    private String charsetEncoding;

    @Override
    public void init() throws ServletException {
        engine = JetWebEngine.create(getServletContext());
        charsetEncoding = engine.getConfig().getOutputEncoding().name();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (response.getContentType() == null) {
            response.setContentType("text/html; charset=" + charsetEncoding);
        }
        response.setCharacterEncoding(charsetEncoding);

        String path = RequestUtils.getPathInfo(request);
        try {
            JetTemplate template = engine.getTemplate(path);
            JetWebContext context = new JetWebContext(request, response);
            template.render(context, response.getOutputStream());
        } catch (ResourceNotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Template not found: " + path);
        }
    }
}
