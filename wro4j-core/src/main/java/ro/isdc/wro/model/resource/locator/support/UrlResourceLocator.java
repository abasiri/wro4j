/*
 * Copyright (C) 2011 Betfair.
 * All rights reserved.
 */
package ro.isdc.wro.model.resource.locator.support;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ro.isdc.wro.model.resource.locator.ResourceLocator;

/**
 * UrlResourceLocator capable to read the resources from some URL. Usually, this uriLocator will be the last in the chain of
 * uriLocators.
 *
 * @author Alex Objelean
 * @created 30 Mar 2011
 * @since 1.4.0
 */
public class UrlResourceLocator
    extends AbstractResourceLocator {
  private static final Logger LOG = LoggerFactory.getLogger(UrlResourceLocator.class);
  /**
   * Path of the resource to be located.
   */
  private final String path;

  /**
   * Create a new UrlResource.
   * @param path a URL path
   * @throws MalformedURLException if the given URL path is not valid
   */
  public UrlResourceLocator(final String path) {
    if (path == null) {
      throw new IllegalArgumentException("path cannot be NULL!");
    }
    this.path = path;
  }

  public UrlResourceLocator(final URL url) {
    if (url == null) {
      throw new IllegalArgumentException("url cannot be NULL!");
    }
    this.path = url.getPath();
  }

  /**
   * {@inheritDoc}
   */
  public InputStream getInputStream()
      throws IOException {
    LOG.debug("Reading path: " + path);
    if (getWildcardStreamLocator().hasWildcard(path)) {
      final String fullPath = FilenameUtils.getFullPath(path);
      final URL url = new URL(fullPath);
      return getWildcardStreamLocator().locateStream(path, new File(url.getFile()));
    }
    final URL url = new URL(path);
    return new BufferedInputStream(url.openStream());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long lastModified() {
    try {
      final URL url = new URL(path);
      final File file = FileUtils.toFile(url);
      return file != null ? file.lastModified() : super.lastModified();
    } catch (final MalformedURLException e) {
      return super.lastModified();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ResourceLocator createRelative(String relativePath)
      throws IOException {
    if (relativePath.startsWith("/")) {
      relativePath = relativePath.substring(1);
    }
    return new UrlResourceLocator(new URL(new URL(this.path), relativePath));
  }
}
