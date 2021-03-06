package argelbargel.jenkins.plugins.gitlab_branch_source.hooks;


import argelbargel.jenkins.plugins.gitlab_branch_source.GitLabSCMNavigator;
import argelbargel.jenkins.plugins.gitlab_branch_source.GitLabSCMSource;
import hudson.Extension;
import hudson.model.Item;
import hudson.model.listeners.ItemListener;
import jenkins.scm.api.SCMNavigator;
import jenkins.scm.api.SCMNavigatorOwner;
import jenkins.scm.api.SCMSource;
import jenkins.scm.api.SCMSourceOwner;
import org.apache.commons.lang.StringUtils;


@Extension
public final class GitLabSCMWebHookItemListener extends ItemListener {
    // TODO: check whether we really need this
    @Override
    public void onCreated(Item item) {
        if (item instanceof SCMSourceOwner) {
            onCreated((SCMSourceOwner) item);
        }
    }

    // TODO: check whether we really need this
    private void onCreated(SCMSourceOwner item) {
        for (SCMSource source : item.getSCMSources()) {
            if (source instanceof GitLabSCMSource) {
                register((GitLabSCMSource) source, item);
            }
        }
    }

    @Override
    public void onDeleted(Item item) {
        if (item instanceof SCMNavigatorOwner) {
            onDeleted((SCMNavigatorOwner) item);
        }

        if (item instanceof SCMSourceOwner) {
            onDeleted((SCMSourceOwner) item);
        }
    }

    private void onDeleted(SCMNavigatorOwner owner) {
        for (SCMNavigator navigator : owner.getSCMNavigators()) {
            if (navigator instanceof GitLabSCMNavigator) {
                unregister((GitLabSCMNavigator) navigator, owner);
            }
        }
    }

    private void onDeleted(SCMSourceOwner owner) {
        for (SCMSource source : owner.getSCMSources()) {
            if (source instanceof GitLabSCMSource) {
                unregister((GitLabSCMSource) source, owner);
            }
        }
    }

    // TODO: check whether we really need this
    @Override
    public void onUpdated(Item item) {
        if (item instanceof SCMNavigatorOwner) {
            onUpdated((SCMNavigatorOwner) item);
        }
    }

    // TODO: check whether we really need this
    private void onUpdated(SCMNavigatorOwner owner) {
        for (SCMNavigator navigator : owner.getSCMNavigators()) {
            if (navigator instanceof GitLabSCMNavigator) {
                onUpdated((GitLabSCMNavigator) navigator, owner);
            }
        }
    }

    // TODO: check whether we really need this
    private void onUpdated(GitLabSCMNavigator navigator, SCMNavigatorOwner owner) {
        if (navigator.saved()) {
            if (navigator.getListenToWebHooks()) {
                register(navigator, owner);
            } else {
                unregister(navigator, owner);
            }
        }
    }

    private void register(GitLabSCMNavigator navigator, SCMNavigatorOwner owner) {
        GitLabSCMWebHook.get().addListener(navigator, owner);
    }

    private void register(GitLabSCMSource source, SCMSourceOwner owner) {
        if (source.getListenToWebHooks()) {
            GitLabSCMWebHook.get().addListener(source, owner);
        }
    }

    private void unregister(GitLabSCMNavigator navigator, SCMNavigatorOwner owner) {
        if (navigator.getListenToWebHooks() && !StringUtils.isEmpty(navigator.getSourceSettings().getConnectionName())) {
            GitLabSCMWebHook.get().removeListener(navigator, owner);
        }
    }

    private void unregister(GitLabSCMSource source, SCMSourceOwner owner) {
        if (source.getListenToWebHooks()) {
            GitLabSCMWebHook.get().removeListener(source, owner);
        }
    }
}
