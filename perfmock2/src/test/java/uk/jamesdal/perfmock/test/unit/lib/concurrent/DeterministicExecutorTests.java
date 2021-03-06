package uk.jamesdal.perfmock.test.unit.lib.concurrent;

import uk.jamesdal.perfmock.Expectations;
import uk.jamesdal.perfmock.Sequence;
import uk.jamesdal.perfmock.api.Action;
import uk.jamesdal.perfmock.lib.concurrent.DeterministicExecutor;
import uk.jamesdal.perfmock.test.unit.internal.MockObjectTestCase;

public class DeterministicExecutorTests extends MockObjectTestCase {
    DeterministicExecutor scheduler = new DeterministicExecutor();
    
    Runnable commandA = mock(Runnable.class, "commandA");
    Runnable commandB = mock(Runnable.class, "commandB");
    Runnable commandC = mock(Runnable.class, "commandC");
    Runnable commandD = mock(Runnable.class, "commandD");
    
    public void testRunsPendingCommands() {
        scheduler.execute(commandA);
        scheduler.execute(commandB);
        
        final Sequence executionOrder = sequence("executionOrder");
        
        checking(new Expectations() {{
            oneOf (commandA).run(); inSequence(executionOrder);
            oneOf (commandB).run(); inSequence(executionOrder);
        }});
        
        scheduler.runPendingCommands();
    }
    
    public void testCanLeaveCommandsSpawnedByExecutedCommandsPendingForLaterExecution() {
        scheduler.execute(commandA);
        scheduler.execute(commandB);
        
        final Sequence executionOrder = sequence("executionOrder");
        
        checking(new Expectations() {{
            oneOf (commandA).run(); inSequence(executionOrder); will(schedule(commandC));
            oneOf (commandB).run(); inSequence(executionOrder); will(schedule(commandD));
            never (commandC).run();
            never (commandD).run();
        }});
        
        scheduler.runPendingCommands();
    }
    
    public void testCanRunCommandsSpawnedByExecutedCommandsUntilNoCommandsArePending() {
        scheduler.execute(commandA);
        scheduler.execute(commandB);
        
        final Sequence executionOrder = sequence("executionOrder");
        
        checking(new Expectations() {{
            oneOf (commandA).run(); inSequence(executionOrder); will(schedule(commandC));
            oneOf (commandB).run(); inSequence(executionOrder); will(schedule(commandD));
            oneOf (commandC).run(); inSequence(executionOrder);
            oneOf (commandD).run(); inSequence(executionOrder);
        }});
        
        scheduler.runUntilIdle();
    }

    protected Action schedule(final Runnable command) {
        return ScheduleOnExecutorAction.schedule(scheduler, command);
    }
}
