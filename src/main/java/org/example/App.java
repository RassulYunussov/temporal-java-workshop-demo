package org.example;

import io.temporal.client.ActivityCompletionClient;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;
import io.temporal.worker.WorkerFactoryOptions;
import io.temporal.worker.WorkerOptions;
import io.temporal.workflow.Workflow;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class App {
    static final String TASK_QUEUE = App.class.getPackageName() + ":" + "MoneyTransfer";
    private static final Logger log = Workflow.getLogger(App.class.getSimpleName());

    public static void main(String[] args) {
        BasicConfigurator.configure();
        // Get a Workflow service stub.
        final WorkflowServiceStubs service = WorkflowServiceStubs.newServiceStubs(WorkflowServiceStubsOptions
                .newBuilder()
                .build());

        /*
         * Get a Workflow service client which can be used to start, Signal, and Query Workflow Executions.
         */
        WorkflowClient client = WorkflowClient.newInstance(service);
        ActivityCompletionClient completionClient = client.newActivityCompletionClient();

        /*
         * Define the workflow factory. It is used to create workflow workers for a specific task queue.
         */
        WorkerFactory factory =
                WorkerFactory.newInstance(client, WorkerFactoryOptions.newBuilder().build());

        /*
         * Define the workflow worker.
         * Workflow workers listen to a defined task queue and process workflows and activities.
         */
        Worker worker = factory.newWorker(TASK_QUEUE, WorkerOptions.newBuilder().build());

        worker.registerWorkflowImplementationTypes(MoneyTransferWorkflowImpl.class);
        worker.registerActivitiesImplementations(new AccountServiceImpl(completionClient));


        CompletableFuture.runAsync(() -> {
            try {
                while (true) {
                    Thread.sleep(5000);
                    final WorkflowOptions options =
                            WorkflowOptions.newBuilder()
                                    .setWorkflowId(UUID.randomUUID().toString())
                                    .setTaskQueue(TASK_QUEUE)
                                    .build();

                    // Create the workflow client stub.
                    // It is used to start our workflow execution.
                    final MoneyTransferWorkflow workflow =
                            client.newWorkflowStub(MoneyTransferWorkflow.class, options);

                    TransferRequest transferRequest =
                            new TransferRequest("fromAccount", "toAccount", 200);
                    final var wf = WorkflowClient.start(workflow::transfer, transferRequest);
                    log.info("Id of new workflow {}", wf.getRunId());
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });


        factory.start();

    }
}
