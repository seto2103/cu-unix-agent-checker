package jenkins.plugins.AgentChecker;

import hudson.Launcher;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;
import hudson.model.Computer;
import java.io.IOException;

import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import jenkins.model.Jenkins;
import java.util.concurrent.TimeUnit;

public class AgentChecker extends Builder implements SimpleBuildStep {

    private String author;

    @DataBoundConstructor
    public AgentChecker(){
        this.author = "Sebastian Torres";
    }


    @Override
    public void perform(Run<?, ?> run, FilePath workspace, EnvVars env, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {

        Computer[] tmp_nodes = Jenkins.getInstanceOrNull().getComputers();

        for(Computer c: tmp_nodes){
            while(c.isOffline()){
                c.connect(false);
                String dname = c.getDisplayName();
                listener.getLogger().println(dname + " - attempting to reconnect");
                TimeUnit.SECONDS.sleep(20);
            }
            if(c.isOnline()){
                String dname = c.getDisplayName();
                listener.getLogger().println(dname+ " connected");
            }
        }
        listener.getLogger().println("All Agents Connected and Running, continuing with the build");
    }

    @Symbol("Agent Status Checker")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Agent Checker";
        }

    }

}
