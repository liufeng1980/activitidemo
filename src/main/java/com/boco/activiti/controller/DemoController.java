package com.boco.activiti.controller;

import com.boco.activiti.service.CallFlowService;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DemoController {
    Logger LOGGER = LoggerFactory.getLogger(DemoController.class);

    @RequestMapping("/index")
    public String index(){
        return "index";
    }

    @Autowired
    CallFlowService callFlowService;

    @ResponseBody
    @RequestMapping("/init")
    public String init(){
        LOGGER.info("start.....................");
        return "init";
    }

    @ResponseBody
    @RequestMapping("/deploy")
    public String deploy(){
        callFlowService.deploy();
        return "流程部署";
    }

    @ResponseBody
    @RequestMapping("/startProcessInstance")
    @Transactional(value = "transactionManager",rollbackFor = Exception.class)
    public String startProcessInstance(){
        //runtimeService.start
        callFlowService.startProcessInstance();
        return "";
    }

    /**
     * 呼叫中心提交订单
     * @return
     */
    @ResponseBody
    @RequestMapping("/submitBill")
    public String submitBill(){
        callFlowService.submitBill();
        return "呼叫中心提交订单";
    }



    /**
     * 2.运营科审核
     * @return
     */
    @ResponseBody
    @RequestMapping("/YunYingKeAudit")
    public String YunYingKeAudit(){
        callFlowService.YunYingKeAudit();
        return "运营科审核";
    }

    /**
     * 3.高路公司审核
     * @return
     */
    @ResponseBody
    @RequestMapping("/gaoLuAudit")
    public String gaoLuAudit(){
        callFlowService.gaoLuAudit();
        return "高路公司审核";
    }

    /**
     * 4.分公司审核
     * @return
     */
    @ResponseBody
    @RequestMapping("/fenZhongXinAudit")
    public String fenZhongXinAudit(){
        callFlowService.fenZhongXinAudit();
        return "高路公司审核";
    }
}
