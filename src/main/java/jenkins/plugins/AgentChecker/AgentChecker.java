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
import java.util.concurrent.TimeUnit;

import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import jenkins.model.Jenkins;

public class AgentChecker extends Builder implements SimpleBuildStep {

    @DataBoundConstructor
    public AgentChecker(){
    }


    @Override
    public void perform(Run<?, ?> run, FilePath workspace, EnvVars env, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {

        Jenkins jenkins = Jenkins.getInstanceOrNull();
        if(jenkins != null){
            Computer[] agents = jenkins.getComputers();
            int attempt_time;
            for(Computer c: agents){
                while(c.isOffline()){
                    attempt_time = 0;
                    String dname = c.getDisplayName();
                    listener.getLogger().println(dname + " - attempting to reconnect");
                    c.connect(true);
                    while(c.isConnecting()){
                        attempt_time++;
                        TimeUnit.SECONDS.sleep(1);
                        if(attempt_time == 30){
                            listener.getLogger().println("Connection Timeout");
                            break;
                        }
                    }
                }
                if(c.isOnline()){
                    String dname = c.getDisplayName();
                    listener.getLogger().println(dname+ " connected");
                }
            }
            listener.getLogger().println("All Agents Connected and Running");
        }
        else{
            listener.getLogger().println("No jenkins instance running");
        }
    }

    @Symbol("agentstatus")
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
