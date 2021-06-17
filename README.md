![PerfMock 2 0 Logo](https://user-images.githubusercontent.com/21280865/121378698-54545b00-c93b-11eb-8f55-83e1e6c077bd.PNG)

# PerfMock 2.0 

PerfMock 2.0 is a fork off https://github.com/jmock-developers/jmock-library by James Dalboth. Implemented as part of his undergraduate individual project for Imperial College London. Inspired off https://github.com/spikeh/perfmock

By extending off JMock, I hope to provide the ability to estimate performance charactsistics of the program undertest in a production environment. By utlising the benefits of unit tests and performance tests together, I want to move performance testing closer to the developer and become a more integral part of the development life cycle. 

# Example

```
@Rule
public PerfRule perfRule = new PerfRule(new ConsoleReportGenerator());

@Rule
public PerfMockery ctx = new PerfMockery(perfRule);

private final ExampleObject exampleObject = ctx.mock(ExampleObject.class);

@Test
@PerfTest(iterations = 2000, warmups = 10)
public void exampleTest() {
    SUT sut = new SUT(exampleObject);

    ctx.checking(new Expectations() {{
        allowing(exampleObject).doSomething(); will(returnValue(null)); taking(seconds(new Uniform(5.0, 10.0)));
    }});

    sut.run();
}
```

The format of a PerfMock 2.0 test is similar to that of a JMock test. First you define the PerfRule and the PerfMockery. Mock Objects are created as usual. Add the PerfTest annotation to mark a test as a performance test along with the number of iterations required. Expections for functionality look the same but use the method taking to mark the performance profile of a mock object invocation.

This is a simple example and PerfMock 2.0 presents a rich variety of features for the user, such as distribution matching, performance modelling and support for concurrent programs.

# Install

To install and use PerfMock 2.0. Install the jar included in install-jars along with JUnit4 into your project. [Version 0.2.2](https://github.com/JamesDalboth/PerfMock2.0/blob/master/install-jars/perfmock2-junit4-0.2.2-SNAPSHOT-jar-with-dependencies.jar)

# Repeat method

The users are not restricted to use the @PerfTest annotation and can achieve similar results like such. This allows them to easily add traditional assertions except this time over performance results.

```
@Rule
public PerfRule perfRule = new PerfRule(new ConsoleReportGenerator());

@Rule
public PerfMockery ctx = new PerfMockery(perfRule);

private final ExampleObject exampleObject = ctx.mock(ExampleObject.class);

@Test
@PerfTest(iterations = 2000, warmups = 10)
public void exampleTest() {
    ctx.repeat(2000, 100, () -> {
        SUT sut = new SUT(exampleObject);
    
        ctx.checking(new Expectations() {{
            allowing(exampleObject).doSomething(); will(returnValue(null)); taking(seconds(new Uniform(5.0, 10.0)));
        }});

        sut.run();
    });
    
    assertThat(ctx.perfResults().meanMeasuredTime(), lessThan(6000.0));
}
```

# PerfRequirements

Like above the users can add performance requirements to tests using the annotation, to achieve this we use the perfrequirement annotation. We can add as many requirements as we want.

```
@Rule
public PerfRule perfRule = new PerfRule(new ConsoleReportGenerator());

@Rule
public PerfMockery ctx = new PerfMockery(perfRule);

private final ExampleObject exampleObject = ctx.mock(ExampleObject.class);

@Test
@PerfTest(iterations = 2000, warmups = 10)
@PerfRequirement(mode = PerfMode.MEAN, comparator = PerfComparator.LESS_THAN, value = 6000)
public void exampleTest() {
    SUT sut = new SUT(exampleObject);

    ctx.checking(new Expectations() {{
        allowing(exampleObject).doSomething(); will(returnValue(null)); taking(seconds(new Uniform(5.0, 10.0)));
    }});

    sut.run();
}
```

# HTML Reports

# Performance Models

The user is free to choose from a variety of performance models, including

1. Constant distributions
2. Uniform distributions
3. Normal distributions
4. Exponential distributions
5. more

The user is also free to implement their own performance model by implementing the perfmodel interface. Doing this will allow the developer to create a performance model which is dependent on how many times it has been invoked or past invokations of other mock objects.

# Using Empircal Models

For hierarchical programs, the users can export data from a test into a csv file. This is done using the CSVGenerator

```
    @Rule
    public PerfRule perfRule = new PerfRule(new CSVGenerator());
```

Later in another test, we can import this data into an empircal model

```
    PerfModel model = new Discrete("csvfile.csv");
```

# Test for programs using threads

// TODO

# Test for program using an executor service

// TODO

# Ignoring sections

occasionally there may be periods of code in the test which the developer may wish to ignore from the calculations. We can do this by wrapping that execution in ignore like such

```
ctx.ignore(() -> {
    \\ code here
});
```
