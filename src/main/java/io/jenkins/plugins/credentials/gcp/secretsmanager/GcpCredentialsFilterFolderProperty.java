package io.jenkins.plugins.credentials.gcp.secretsmanager;

import com.cloudbees.hudson.plugins.folder.AbstractFolderProperty;
import com.cloudbees.hudson.plugins.folder.AbstractFolderPropertyDescriptor;
import com.cloudbees.hudson.plugins.folder.Folder;
import hudson.Extension;
import org.kohsuke.stapler.DataBoundConstructor;

public class GcpCredentialsFilterFolderProperty extends AbstractFolderProperty<Folder> {

    private final String filter;

    private final boolean exclusive;

    @DataBoundConstructor
    public GcpCredentialsFilterFolderProperty(String filter, Boolean exclusive) {
        this.filter = filter;
        this.exclusive = exclusive;
    }

    public String getFilter() {
        return filter;
    }

    public boolean isExclusive() {
        return exclusive;
    }

    @Extension(optional = true)
    public static class DescriptorImpl extends AbstractFolderPropertyDescriptor {

        /**
         * {@inheritDoc}
         */
        @Override
        public String getDisplayName() {
            return Messages.GcpCredentialsFilterFolderProperty_DisplayName();
        }

    }
}
