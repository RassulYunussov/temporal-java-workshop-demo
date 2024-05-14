package org.example;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

/**
 * Workflow interface.
 */
@WorkflowInterface
public interface MoneyTransferWorkflow {


    @WorkflowMethod
    void transfer(TransferRequest transferRequest);
}