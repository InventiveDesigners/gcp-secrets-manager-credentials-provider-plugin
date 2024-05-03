package io.jenkins.plugins.credentials.gcp.secretsmanager;

import java.io.Serializable;
import java.util.Objects;

public class FilterProperty implements Serializable {
    private final String additionalFilter;
    private final boolean exclusive;
    private final String serverFilter;
    private final String project;

    public FilterProperty(String project, String serverFilter, String filter, boolean exclusive) {
        this.project = project;
        this.additionalFilter = filter;
        this.exclusive = exclusive;
        this.serverFilter = serverFilter;
    }

    public String getProject() {
        return project;
    }

    public String getAdditionalFilter() {
        return additionalFilter;
    }

    public boolean isExclusive() {
        return exclusive;
    }

    public String getServerFilter() {
        return serverFilter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilterProperty that = (FilterProperty) o;
        return exclusive == that.exclusive && Objects.equals(additionalFilter, that.additionalFilter) && Objects.equals(serverFilter, that.serverFilter) && Objects.equals(project, that.project);
    }

    @Override
    public int hashCode() {
        return Objects.hash(additionalFilter, exclusive, serverFilter, project);
    }
}
