package com.boco.activiti.service;

import com.boco.activiti.bean.ProcessAuditResult;
import com.boco.activiti.controller.DemoController;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CallFlowService {
    Logger LOGGER = LoggerFactory.getLogger(CallFlowService.class);
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

    public String init(){
        LOGGER.info("start.....................");
        LOGGER.info("rs="+rs);
        return "init";
    }


    @Transactional(value = "transactionManager",rollbackFor = Exception.class)
    public String deploy(){
        LOGGER.info("deploy.....................");
        DeploymentBuilder deploymentBuilder = rs.createDeployment();
        deploymentBuilder.addClasspathResource("call_flow_process.bpmn20.xml");
        Deployment deployment = deploymentBuilder.deploy();
        String deploymentId = deployment.getId();
        ProcessDefinition processDefinition = rs.createProcessDefinitionQuery().deploymentId(deploymentId).singleResult();
        LOGGER.info("流程定义文件 [{}],流程定义ID [{}]", processDefinition.getName(), processDefinition.getId());
        return "deploy";
    }

    /**
     * 启动流程实例   转入呼叫中心提交订单实现
     * @return
     */
    @Transactional(value = "transactionManager",rollbackFor = Exception.class)
    public String startProcessInstance(){
        //runtimeService.start
//        String inputUser ="zhangsan";
//        Map<String,Object> map = new HashMap<>();
//        map.put("inputUser",inputUser);
//        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(CALL_FLOW_PROCESS_NAME,businessKey,map);
//        LOGGER.info("流程定义ID:{}",processInstance.getProcessDefinitionId());
//        LOGGER.info("流程实例ID:{}",processInstance.getId());

        return "";
    }

    /**
     * 呼叫中心提交订单
     * @return
     */
    @Transactional(value = "transactionManager",rollbackFor = Exception.class)
    public String submitBill(){
        String inputUser ="zhangsan";
        Map<String,Object> map = new HashMap<>();
        map.put("inputUser",inputUser);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(CALL_FLOW_PROCESS_NAME,businessKey,map);
        LOGGER.info("流程定义ID:{}",processInstance.getProcessDefinitionId());
        LOGGER.info("流程实例ID:{}",processInstance.getId());
        String instanceId = processInstance.getProcessInstanceId();
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
    @Transactional(value = "transactionManager",rollbackFor = Exception.class)
    public String YunYingKeAudit(){
        List<Task> taskList = taskService.createTaskQuery()
                .taskCandidateGroup("yuyingke")
                .processInstanceBusinessKey(businessKey).list();

        if(taskList == null){
            LOGGER.info("未查询到运营科任务");
            return "未查询到运营科任务";
        }
        for (Task task : taskList) {
            LOGGER.info("运营科任务id:{}",task.getId());
            LOGGER.info("运营科任务名称:{}",task.getName());

            //拾取任务
            try {
                taskService.claim(task.getId(),"lisi");
                Map<String,Object> map = new HashMap<>();
                map.put("gaolu","diyigaolu");
                ProcessAuditResult result = new ProcessAuditResult();
                result.setResultCode(1);
                map.put("result",result);

                //taskService.setVariable(task.getId(),"result",1);
                taskService.complete(task.getId(),map);
                LOGGER.info("运营科处理完成");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return "";
    }


    /**
     * 高路公司审核
     */
    @Transactional(value = "transactionManager",rollbackFor = Exception.class)
    public void gaoLuAudit() {
        List<Task> tasks = taskService.createTaskQuery()
                .processDefinitionKey(CALL_FLOW_PROCESS_NAME)
                .taskCandidateGroup("diyigaolu").list();
        if(tasks == null){
            LOGGER.info("未查询到高路公司审核任务");
            return;
        }
        for (Task task:tasks){
            try {
                taskService.claim(task.getId(),"wangwu");
                Map<String,Object> map = new HashMap<>();
                map.put("fengongsi","diyifengongsi");
                //taskService.setVariable(task.getId(),"result",1);
                taskService.complete(task.getId(),map);
                LOGGER.info("高路公司审核");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 4.分公司审核
     * @return
     */
    public void fenZhongXinAudit() {
        List<Task> tasks = taskService.createTaskQuery()
                .processDefinitionKey(CALL_FLOW_PROCESS_NAME)
                .taskCandidateGroup("diyifengongsi").list();
        if(tasks == null){
            LOGGER.info("未查询到分公司审核任务");
            return;
        }
        for (Task task:tasks){
            try {
                taskService.claim(task.getId(),"zhaoliu");
                Map<String,Object> map = new HashMap<>();
                ProcessAuditResult result = new ProcessAuditResult();
                result.setContineProcessCode(0);
                map.put("result",result);
                map.put("gaolu","diyigaolu");
                //taskService.setVariable(task.getId(),"result",1);
                taskService.complete(task.getId(),map);
                LOGGER.info("分公司审核");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
