/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.ext.datasource.client.common.pager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.user.cellview.client.SimplePager;

/**
 * {@link SimplePager} implementation with customized buttons.
 * 
 * @author "Mickaël Leduque"
 */
public class Pager extends SimplePager {

    private static final int          DEFAULT_FAST_FORWARD_ROWS = 1000;
    private static final TextLocation DEFAULT_TEXT_LOCATION     = TextLocation.CENTER;

    private static Resources          DEFAULT_RESOURCES;

    public Pager() {
        super(DEFAULT_TEXT_LOCATION);
    }

    public Pager(final TextLocation location) {
        this(location, getDefaultResources(), true, DEFAULT_FAST_FORWARD_ROWS, false);
    }

    public Pager(final TextLocation location, final Resources resources, final boolean showFastForwardButton,
                 final int fastForwardRows, final boolean showLastPageButton) {
        this(location, resources, showFastForwardButton, fastForwardRows, showLastPageButton,
             GWT.<ImageButtonsConstants> create(ImageButtonsConstants.class));
    }

    public Pager(final TextLocation location, final Resources resources, final boolean showFastForwardButton,
                 final int fastForwardRows, final boolean showLastPageButton,
                 final ImageButtonsConstants imageButtonConstants) {
        this(location, resources, showFastForwardButton, fastForwardRows, showLastPageButton, true, imageButtonConstants);
    }


    public Pager(final TextLocation location, final Resources resources, final boolean showFastForwardButton,
                 final int fastForwardRows, final boolean showLastPageButton, final boolean showFirstPageButton,
                 final ImageButtonsConstants imageButtonConstants) {
        super(location, resources, showFastForwardButton, fastForwardRows,
              showLastPageButton, showFirstPageButton, imageButtonConstants);
    }

    public Pager(final TextLocation location, final boolean showFastForwardButton, final boolean showLastPageButton) {
        super(location, getDefaultResources(), showFastForwardButton, DEFAULT_FAST_FORWARD_ROWS, showLastPageButton);
    }

    public Pager(final boolean showFastForwardButton, final boolean showLastPageButton) {
        super(DEFAULT_TEXT_LOCATION, getDefaultResources(), showFastForwardButton, DEFAULT_FAST_FORWARD_ROWS, showLastPageButton);
    }

    public Pager(final TextLocation location, final boolean showFastForwardButton,
                 final int fastForwardRows, final boolean showLastPageButton) {
        super(location, getDefaultResources(), showFastForwardButton, fastForwardRows, showLastPageButton);
    }

    public Pager(final boolean showFastForwardButton, final int fastForwardRows, final boolean showLastPageButton) {
        super(DEFAULT_TEXT_LOCATION, getDefaultResources(), showFastForwardButton, fastForwardRows, showLastPageButton);
    }

    /**
     * Creates an instance of the resources.
     * 
     * @return the resources instance for this pager
     */
    private static Resources getDefaultResources() {
        if (DEFAULT_RESOURCES == null) {
            DEFAULT_RESOURCES = GWT.create(PagerResources.class);
        }
        return DEFAULT_RESOURCES;
    }

    public static interface PagerResources extends SimplePager.Resources {

        @Override
        @Source("fastforward-white.png")
        @ImageOptions(flipRtl = true)
        ImageResource simplePagerFastForward();


        @Override
        @Source("fastforward-white.png")
        @ImageOptions(flipRtl = true)
        ImageResource simplePagerFastForwardDisabled();


        @Override
        @Source("stepbackward-white.png")
        @ImageOptions(flipRtl = true)
        ImageResource simplePagerFirstPage();


        @Override
        @Source("stepbackward-black.png")
        @ImageOptions(flipRtl = true)
        ImageResource simplePagerFirstPageDisabled();


        @Override
        @Source("stepforward-white.png")
        @ImageOptions(flipRtl = true)
        ImageResource simplePagerLastPage();


        @Override
        @Source("stepforward-black.png")
        @ImageOptions(flipRtl = true)
        ImageResource simplePagerLastPageDisabled();


        @Override
        @Source("forward-white.png")
        @ImageOptions(flipRtl = true)
        ImageResource simplePagerNextPage();


        @Override
        @Source("forward-black.png")
        @ImageOptions(flipRtl = true)
        ImageResource simplePagerNextPageDisabled();


        @Override
        @Source("backward-white.png")
        @ImageOptions(flipRtl = true)
        ImageResource simplePagerPreviousPage();


        @Override
        @Source("backward-black.png")
        @ImageOptions(flipRtl = true)
        ImageResource simplePagerPreviousPageDisabled();
    }
}
