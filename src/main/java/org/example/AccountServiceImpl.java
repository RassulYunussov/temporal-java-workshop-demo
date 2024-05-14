package org.example;

import io.temporal.activity.Activity;
import io.temporal.activity.ActivityExecutionContext;
import io.temporal.client.ActivityCompletionClient;
import io.temporal.workflow.Workflow;
import org.slf4j.Logger;

import java.util.concurrent.CompletableFuture;

public class AccountServiceImpl implements AccountService {
    private final Logger log = Workflow.getLogger(AccountServiceImpl.class.getSimpleName());
    private final ActivityCompletionClient completionClient;

    public AccountServiceImpl(ActivityCompletionClient completionClient) {
        this.completionClient = completionClient;
    }

    @Override
    public void deposit(DepositRequest depositRequest) {
        ActivityExecutionContext context = Activity.getExecutionContext();
        CompletableFuture.runAsync(() -> {
            log.info("Init deposit: {}", depositRequest);
            //throw new RuntimeException("bam");
        }).whenComplete((result, error) -> {
            if (error != null) {
                completionClient.completeExceptionally(context.getTaskToken(), new RuntimeException(error));
            } else {
                completionClient.complete(context.getTaskToken(), null);
            }
        });
        context.doNotCompleteOnReturn();
    }

    @Override
    public void withdraw(WithdrawRequest withdrawRequest) {
        log.info("Init withdraw: {}", withdrawRequest);
    }
}
