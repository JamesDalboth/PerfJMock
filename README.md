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

To install and use PerfMock 2.0. Install the 2 jars included in install-jars along with JUnit4 into your project.
