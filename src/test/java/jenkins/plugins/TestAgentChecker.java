package jenkins.plugins;
import jenkins.plugins.AgentChecker.AgentChecker;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Label;
import hudson.slaves.DumbSlave;

import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

public class TestAgentChecker {

    @Rule
    public JenkinsRule jenkins = new JenkinsRule();

    @Test
    public void testOnlineSlaves() throws Exception {
        FreeStyleProject project = jenkins.createFreeStyleProject();
        AgentChecker builder = new AgentChecker();
        project.getBuildersList().add(builder);

        DumbSlave agent = jenkins.createOnlineSlave();
        DumbSlave agent_two = jenkins.createOnlineSlave();
        FreeStyleBuild build = jenkins.buildAndAssertSuccess(project);
        jenkins.assertLogContains("All Agents Connected and Running", build);
    }

    @Test
    public void testOfflineSlaves() throws Exception {
        FreeStyleProject project = jenkins.createFreeStyleProject();
        AgentChecker builder = new AgentChecker();
        project.getBuildersList().add(builder);

        DumbSlave agent = jenkins.createSlave();
        DumbSlave agent_two = jenkins.createSlave();
        DumbSlave agent_three = jenkins.createSlave();
        DumbSlave agent_four = jenkins.createSlave();
        FreeStyleBuild build = jenkins.buildAndAssertSuccess(project);
        jenkins.assertLogContains("All Agents Connected and Running", build);
    }

}