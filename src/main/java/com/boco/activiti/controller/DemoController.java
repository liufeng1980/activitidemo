package com.boco.activiti.controller;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DemoController {
    Logger LOGGER = LoggerFactory.getLogger(DemoController.class);
    private static final String CALL_FLOW_PROCESS_NAME ="call_flow_process";
    private String businessKey = "1002";
    @RequestMapping("/index")
    public String index(){
        return "index";
    }

    @Autowired
    RepositoryService rs;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;

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
        deploymentBuilder.addClasspathResource("call_flow_process.bpmn20.xml");
        //deploymentBuilder.addClasspathResource("second_approve.bpmn20.xml");
        Deployment deployment = deploymentBuilder.deploy();
        String deploymentId = deployment.getId();
        ProcessDefinition processDefinition = rs.createProcessDefinitionQuery().deploymentId(deploymentId).singleResult();
        LOGGER.info("流程定义文件 [{}],流程定义ID [{}]", processDefinition.getName(), processDefinition.getId());
        return "deploy";
    }

    @ResponseBody
    @RequestMapping("/startProcessInstance")
    public String startProcessInstance(){
        //runtimeService.start
        String inputUser ="zhangsan";
        Map<String,Object> map = new HashMap<>();
        map.put("inputUser",inputUser);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(CALL_FLOW_PROCESS_NAME,businessKey,map);
        LOGGER.info("流程定义ID:{}",processInstance.getProcessDefinitionId());
        LOGGER.info("流程实例ID:{}",processInstance.getId());
        return "";
    }

    /**
     * 呼叫中心提交订单
     * @return
     */
    @ResponseBody
    @RequestMapping("/submitBill")
    public String submitBill(){
        String instanceId = "5001";
        Task task = queryTaskByInstanceId(instanceId);
        if(task != null){
            taskService.complete(task.getId());
            LOGGER.info("完成任务id:{}",task.getId());
            LOGGER.info("完成任务名称:{}",task.getName());
        }
        return "";
    }

    private Task queryTaskByInstanceId(String instanceId) {
        Task task = taskService.createTaskQuery().processInstanceId(instanceId).singleResult();
        if(task == null){
            LOGGER.info("未查询到实例ID:   {}的任务",instanceId);
            return task;
        }
        return task;
    }

    /**
     * 2.运营科审核
     * @return
     */
    @ResponseBody
    @RequestMapping("/YunYingKeAudit")
    public String YunYingKeAudit(){
        List<Task> taskList = taskService.createTaskQuery().taskCandidateGroup("yuyingke")
                .processInstanceBusinessKey(businessKey).list();

        if(taskList == null){
            LOGGER.info("未查询到运营科任务");
            return "未查询到运营科任务";
        }
        for (Task task : taskList) {
            LOGGER.info("运营科任务id:{}",task.getId());
            LOGGER.info("运营科任务名称:{}",task.getName());

            task.setAssignee("lisi");

        }
        return "";
    }





}
