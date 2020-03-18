package com.boco.activiti.controller;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DemoController {
    Logger LOGGER = LoggerFactory.getLogger(DemoController.class);
    @RequestMapping("/index")
    public String index(){
        return "index";
    }

    @Autowired
    RepositoryService rs;

    @ResponseBody
    @RequestMapping("/init")
    public String init(){
        LOGGER.info("start.....................");
        LOGGER.info("rs="+rs);
        return "init";
    }

    @ResponseBody
    @RequestMapping("/deploy")
    public String deploy(){
        LOGGER.info("deploy.....................");
        DeploymentBuilder deploymentBuilder = rs.createDeployment();
        deploymentBuilder.addClasspathResource("second_approve.bpmn20.xml");
        Deployment deployment = deploymentBuilder.deploy();
        String deploymentId = deployment.getId();
        ProcessDefinition processDefinition = rs.createProcessDefinitionQuery().deploymentId(deploymentId).singleResult();
        LOGGER.info("流程定义文件 [{}],流程定义ID [{}]", processDefinition.getName(), processDefinition.getId());
        return "deploy";
    }

}
