package io.jenkins.plugins.credentials.gcp.secretsmanager;

import com.cloudbees.hudson.plugins.folder.Folder;
import com.cloudbees.plugins.credentials.Credentials;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.CredentialsStore;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.google.common.base.Suppliers;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import hudson.Extension;
import hudson.model.ItemGroup;
import hudson.model.ModelObject;
import hudson.security.ACL;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import io.jenkins.plugins.credentials.gcp.secretsmanager.config.PluginConfiguration;
import jenkins.model.Jenkins;
import org.acegisecurity.Authentication;

@Extension
public class GcpCredentialsProvider extends CredentialsProvider {

  public final GcpCredentialsStore gcpCredentialsStore = new GcpCredentialsStore(this);

  final Function<FilterProperty, Collection<StandardCredentials>> credentialsSupplier = new Function<FilterProperty, Collection<StandardCredentials>>() {
    private Map<FilterProperty,Supplier<Collection<StandardCredentials>>> suppliers = new HashMap<>();

    @Override
    public Collection<StandardCredentials> apply(FilterProperty s) {
      synchronized (suppliers) {
        return suppliers.computeIfAbsent(s, k -> memoizeWithExpiration(CredentialsSupplier.standard(k), Duration.ofMinutes(5))).get();
      }
    }
  };

  private static <T> Supplier<T> memoizeWithExpiration(Supplier<T> base, Duration duration) {
    return Suppliers.memoizeWithExpiration(base::get, duration.toMillis(), TimeUnit.MILLISECONDS)::get;
  }

  @NonNull
  @Override
  public <C extends Credentials> List<C> getCredentials(
      @NonNull Class<C> type,
      @Nullable ItemGroup itemGroup,
      @Nullable Authentication authentication) {
    if (ACL.SYSTEM.equals(authentication)) {

      PluginConfiguration configuration = PluginConfiguration.getInstance();

      FilterProperty additionalFilter;
      if(itemGroup instanceof Folder) {
        Folder f = (Folder) itemGroup;
        GcpCredentialsFilterFolderProperty filter = f.getProperties().get(GcpCredentialsFilterFolderProperty.class);
        additionalFilter = new FilterProperty(configuration.getProject(), configuration.getServerSideFilter().getFilter(), filter.getFilter(), filter.isExclusive());
      }
      else {
        additionalFilter = new FilterProperty(configuration.getProject(), configuration.getServerSideFilter().getFilter(), "", false);
      }

      final Collection<StandardCredentials> credentials = credentialsSupplier.apply(additionalFilter);

      return credentials.stream()
          .filter(c -> type.isAssignableFrom(c.getClass()))
          .map(type::cast)
          .collect(Collectors.toList());
    }

    return Collections.emptyList();
  }

  @Override
  public CredentialsStore getStore(ModelObject object) {
    return object == Jenkins.get() ? gcpCredentialsStore : null;
  }

  @Override
  public String getIconClassName() {
    return "icon-gcp-secrets-manager-credentials-store";
  }
}
