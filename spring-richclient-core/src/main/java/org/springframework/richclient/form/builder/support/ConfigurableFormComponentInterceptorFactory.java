/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.richclient.form.builder.support;

import java.util.Arrays;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.builder.FormComponentInterceptor;
import org.springframework.richclient.form.builder.FormComponentInterceptorFactory;
import org.springframework.util.Assert;

/**
 * <code>FormComponentInterceptorFactory</code> implementation that can include or
 * exclude form models.
 * 
 * @author Peter De Bruycker
 */
public abstract class ConfigurableFormComponentInterceptorFactory implements FormComponentInterceptorFactory,
        InitializingBean {
    private String[] excludedFormModelIds;

    private String[] includedFormModelIds;

    /**
     * Returns a <code>FormComponentInterceptor</code> for the given
     * <code>FormModel</code>. Checks against the included and excluded
     * <code>FormModel</code> ids.
     * <p>
     * If the excluded ids are specified and contain the given form model, returns
     * <code>null</code>. <br>
     * If the included ids are specified and don't contain the given form model, returns
     * <code>null</code>. <br>
     * 
     * @param formModel the <code>FormModel</code>
     * @return a <code>FormComponentInterceptor</code> for the given
     *         <code>FormModel</code>
     */
    public final FormComponentInterceptor getInterceptor( FormModel formModel ) {
        // if excludedFormModelIds have been specified, use this to check if the
        // form is excluded
        if( excludedFormModelIds != null && Arrays.asList( excludedFormModelIds ).contains( formModel.getId() ) ) {
            return null;
        }

        // if includedFormModelIds have been specified, use this to check if the
        // form is included
        if( includedFormModelIds != null && !Arrays.asList( includedFormModelIds ).contains( formModel.getId() ) ) {
            return null;
        }

        // if we fall through, create an interceptor
        return createInterceptor( formModel );
    }

    /**
     * Returns a <code>FormComponentInterceptor</code> for the given
     * <code>FormModel</code>.
     * 
     * @param formModel the <code>FormModel</code>
     * @return the <code>FormComponentInterceptor</code>
     */
    protected abstract FormComponentInterceptor createInterceptor( FormModel formModel );

    public void afterPropertiesSet() throws Exception {
        Assert.state( excludedFormModelIds == null || includedFormModelIds == null,
                "only one of excludedFormModelIds or includedFormModelIds can be given" );
    }

    /**
     * Returns the excluded <code>FormModel</code> ids.
     * 
     * @return the excluded ids
     */
    public String[] getExcludedFormModelIds() {
        return excludedFormModelIds;
    }

    /**
     * Sets the excluded <code>FormModel</code> ids.
     * <p>
     * Either <code>excludedFormModelIds</code> or <code>includedFormModelIds</code>
     * should be set.
     * 
     * @param excludedFormModelIds the excluded ids
     */
    public void setExcludedFormModelIds( String[] excludedFormModelIds ) {
        this.excludedFormModelIds = excludedFormModelIds;
    }

    /**
     * Returns the included <code>FormModel</code> ids.
     * 
     * @return the included ids
     */
    public String[] getIncludedFormModelIds() {
        return includedFormModelIds;
    }

    /**
     * Sets the included <code>FormModel</code> ids.
     * <p>
     * Either <code>excludedFormModelIds</code> or <code>includedFormModelIds</code>
     * should be set.
     * 
     * @param includedFormModelIds the excluded ids
     */
    public void setIncludedFormModelIds( String[] includedFormModelIds ) {
        this.includedFormModelIds = includedFormModelIds;
    }
}
