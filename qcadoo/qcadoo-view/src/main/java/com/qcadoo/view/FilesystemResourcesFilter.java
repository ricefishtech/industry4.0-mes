package com.qcadoo.view;

import com.google.common.collect.Sets;
import org.apache.commons.io.IOUtils;

import com.qcadoo.localization.internal.ConfigUtil;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

public class FilesystemResourcesFilter implements Filter {

    private final Set EXTENSIONS = Sets.newHashSet("js", "css");

    @Autowired
    private ConfigUtil configUtil;

    @Override
    public void init(FilterConfig conf) {
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, conf.getServletContext());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        if (configUtil.isHotDeploy() && request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;

            if (!serveResource(httpRequest, (HttpServletResponse) response)) {
                chain.doFilter(request, response);
            }

        } else {
            chain.doFilter(request, response);
        }
    }

    public boolean serveResource(final HttpServletRequest request, final HttpServletResponse response) {
        InputStream resource = getResourceFromURI(request.getRequestURI());

        if (resource != null) {
            response.setContentType(getContentTypeFromURI(request));
            try {
                IOUtils.copy(resource, response.getOutputStream());
            } catch (IOException e) {
                throw new IllegalStateException(e.getMessage(), e);
            } finally {
                try {
                    resource.close();
                } catch (IOException ignore) {
                }
            }
            return true;
        }
        return false;
    }

    private InputStream getResourceFromURI(final String uri) {
        int dotIndex = uri.lastIndexOf('.');

        if (dotIndex != -1) {
            String extension = uri.substring(dotIndex + 1);

            if (EXTENSIONS.contains(extension)) {
                List<String> prefixes = Arrays.asList("/mes/mes-plugins/", "/mes-commercial/", "/qcadoo/");

                for (String prefix : prefixes) {
                    Path dir = FileSystems.getDefault().getPath(configUtil.getSourceBasePath() + prefix);
                    try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                        for (Path pluginMainDir : stream) {
                            Path file = pluginMainDir.resolve("src/main/resources/").resolve(uri.substring(1));
                            if (Files.exists(file)) {
                                return file.toUri().toURL().openStream();
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return null;
    }

    private String getContentTypeFromURI(final HttpServletRequest request) {
        String[] arr = request.getRequestURI().split("\\.");
        String ext = arr[arr.length - 1];
        if ("js".equals(ext)) {
            return "text/javascript";
        } else if ("css".equals(ext)) {
            return "text/css";
        } else {
            return URLConnection.guessContentTypeFromName(request.getRequestURL().toString());
        }
    }

    @Override
    public void destroy() {
    }
}
