/**
 * ***************************************************************************
 * Copyright (c) 2010 Qcadoo Limited
 * Project: Qcadoo Framework
 * Version: 1.4
 *
 * This file is part of Qcadoo.
 *
 * Qcadoo is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.qcadoo.plugin.internal.aop;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.qcadoo.plugin.api.PluginStateResolver;
import com.qcadoo.plugin.api.RunIfEnabled;
import com.qcadoo.plugin.internal.PluginUtilsService;

public class RunIfEnabledTest {

    private static final String PLUGIN_NAME = "somePlugin";

    private static final String SECOND_PLUGIN_NAME = "someSecondPlugin";

    @Mock
    private DependencyMock dependencyMock;

    private static AspectDependencyMock aspectDependencyMock;

    private PluginStateResolver pluginStateResolver;

    private interface DependencyMock {

        void run();

    }

    private interface AspectDependencyMock {

        void runBefore();

        void runAround();

        void runAfter();

    }

    private static class MethodLevelAnnotatedClass {

        private DependencyMock dependencyMock;

        public MethodLevelAnnotatedClass(final DependencyMock dependencyMock) {
            this.dependencyMock = dependencyMock;
        }

        @RunIfEnabled(PLUGIN_NAME)
        public void run() {
            dependencyMock.run();
        }

    }

    @RunIfEnabled(PLUGIN_NAME)
    private static class ClassLevelAnnotatedClass {

        private DependencyMock dependencyMock;

        public ClassLevelAnnotatedClass(final DependencyMock dependencyMock) {
            this.dependencyMock = dependencyMock;
        }

        public void run() {
            dependencyMock.run();
        }
    }

    @RunIfEnabled({ PLUGIN_NAME, SECOND_PLUGIN_NAME })
    private static class ClassLevelAnnotatedWithManyPlugins {

        private DependencyMock dependencyMock;

        public ClassLevelAnnotatedWithManyPlugins(final DependencyMock dependencyMock) {
            this.dependencyMock = dependencyMock;
        }

        public void run() {
            dependencyMock.run();
        }
    }

    private static class MethodLevelAnnotatedWithManyPlugins {

        private DependencyMock dependencyMock;

        public MethodLevelAnnotatedWithManyPlugins(final DependencyMock dependencyMock) {
            this.dependencyMock = dependencyMock;
        }

        @RunIfEnabled({ PLUGIN_NAME, SECOND_PLUGIN_NAME })
        public void run() {
            dependencyMock.run();
        }
    }

    @RunIfEnabled({ PLUGIN_NAME })
    private static class MethodAndClassLevelAnnotated {

        private DependencyMock dependencyMock;

        public MethodAndClassLevelAnnotated(final DependencyMock dependencyMock) {
            this.dependencyMock = dependencyMock;
        }

        @RunIfEnabled({ SECOND_PLUGIN_NAME })
        public void run() {
            dependencyMock.run();
        }
    }

    private static class ClassWithoutAnnotations {

        private DependencyMock dependencyMock;

        public ClassWithoutAnnotations(final DependencyMock dependencyMock) {
            this.dependencyMock = dependencyMock;
        }

        public void runFirst() {
            dependencyMock.run();
        }

        public void runSecond() {
            dependencyMock.run();
        }

        public void runThird(final Object arg) {
            dependencyMock.run();
        }

        public void runFourth() {
            dependencyMock.run();
        }
    }

    private static class ClassWithRegularMethodExpectingPjpArgument {

        @RunIfEnabled(PLUGIN_NAME)
        public void doSthg(final ProceedingJoinPoint pjp) throws Throwable {
            pjp.proceed();
        }

        @RunIfEnabled(PLUGIN_NAME)
        public void doSthgWithManyArgs(final ProceedingJoinPoint pjp, final Object secondArg) throws Throwable {
            pjp.proceed();
        }

    }

    @SuppressWarnings("deprecation")
    @Before
    public final void init() {
        MockitoAnnotations.initMocks(this);
        pluginStateResolver = mock(PluginStateResolver.class);
        PluginUtilsService pluginUtilsService = new PluginUtilsService(pluginStateResolver);
        given(pluginStateResolver.isEnabled(Mockito.anyString())).willReturn(false);
        given(pluginStateResolver.isEnabledOrEnabling(Mockito.anyString())).willReturn(false);
        pluginUtilsService.init();

        aspectDependencyMock = mock(AspectDependencyMock.class);
    }

    @Test
    public final void shouldNotRunWithMethodLevelAnnotation() {
        // given
        MethodLevelAnnotatedClass object = new MethodLevelAnnotatedClass(dependencyMock);

        // when
        object.run();

        // then
        verify(pluginStateResolver).isEnabled(PLUGIN_NAME);
        verify(dependencyMock, never()).run();
    }

    @Test
    public final void shouldRunWithMethodLevelAnnotation() {
        // given
        enablePlugin(PLUGIN_NAME);
        MethodLevelAnnotatedClass object = new MethodLevelAnnotatedClass(dependencyMock);

        // when
        object.run();

        // then
        verify(pluginStateResolver).isEnabled(PLUGIN_NAME);
        verify(dependencyMock).run();
    }

    @Test
    public final void shouldNotRunWithClassLevelAnnotation() {
        // given
        ClassLevelAnnotatedClass object = new ClassLevelAnnotatedClass(dependencyMock);

        // when
        object.run();

        // then
        verify(pluginStateResolver).isEnabled(PLUGIN_NAME);
        verify(dependencyMock, never()).run();
    }

    @Test
    public final void shouldRunWithClassLevelAnnotation() {
        // given
        enablePlugin(PLUGIN_NAME);
        ClassLevelAnnotatedClass object = new ClassLevelAnnotatedClass(dependencyMock);

        // when
        object.run();

        // then
        verify(pluginStateResolver).isEnabled(PLUGIN_NAME);
        verify(dependencyMock).run();
    }

    @Test
    public final void shouldRunClassLevelAnnotationWithManyPlugins() {
        // given
        enablePlugin(PLUGIN_NAME);
        enablePlugin(SECOND_PLUGIN_NAME);

        ClassLevelAnnotatedWithManyPlugins object = new ClassLevelAnnotatedWithManyPlugins(dependencyMock);

        // when
        object.run();

        // then
        verify(pluginStateResolver).isEnabled(PLUGIN_NAME);
        verify(pluginStateResolver).isEnabled(SECOND_PLUGIN_NAME);
        verify(dependencyMock).run();
    }

    @Test
    public final void shouldNotRunClassLevelAnnotationWithManyPlugins() {
        // given
        enablePlugin(PLUGIN_NAME);

        ClassLevelAnnotatedWithManyPlugins object = new ClassLevelAnnotatedWithManyPlugins(dependencyMock);

        // when
        object.run();

        // then
        verify(pluginStateResolver).isEnabled(PLUGIN_NAME);
        verify(pluginStateResolver).isEnabled(SECOND_PLUGIN_NAME);
        verify(dependencyMock, never()).run();
    }

    @Test
    public final void shouldRunMethodLevelAnnotationWithManyPlugins() {
        // given
        enablePlugin(PLUGIN_NAME);
        enablePlugin(SECOND_PLUGIN_NAME);

        MethodLevelAnnotatedWithManyPlugins object = new MethodLevelAnnotatedWithManyPlugins(dependencyMock);

        // when
        object.run();

        // then
        verify(pluginStateResolver).isEnabled(PLUGIN_NAME);
        verify(pluginStateResolver).isEnabled(SECOND_PLUGIN_NAME);
        verify(dependencyMock).run();
    }

    @Test
    public final void shouldNotRunMethodLevelAnnotationWithManyPlugins() {
        // given
        enablePlugin(PLUGIN_NAME);

        MethodLevelAnnotatedWithManyPlugins object = new MethodLevelAnnotatedWithManyPlugins(dependencyMock);

        // when
        object.run();

        // then
        verify(pluginStateResolver).isEnabled(PLUGIN_NAME);
        verify(pluginStateResolver).isEnabled(SECOND_PLUGIN_NAME);
        verify(dependencyMock, never()).run();
    }

    @Test
    public final void shouldRunAndIgnoreClassLevelAnnotationIfMethodIsAlsoAnnotated() {
        // given
        enablePlugin(SECOND_PLUGIN_NAME);

        MethodAndClassLevelAnnotated object = new MethodAndClassLevelAnnotated(dependencyMock);

        // when
        object.run();

        // then
        verify(pluginStateResolver, never()).isEnabled(PLUGIN_NAME);
        verify(pluginStateResolver).isEnabled(SECOND_PLUGIN_NAME);
        verify(dependencyMock).run();
    }

    @Test
    public final void shouldNotRunAndIgnoreClassLevelAnnotationIfMethodIsAlsoAnnotated() {
        // given
        enablePlugin(PLUGIN_NAME);

        MethodAndClassLevelAnnotated object = new MethodAndClassLevelAnnotated(dependencyMock);

        // when
        object.run();

        // then
        verify(pluginStateResolver, never()).isEnabled(PLUGIN_NAME);
        verify(pluginStateResolver).isEnabled(SECOND_PLUGIN_NAME);
        verify(dependencyMock, never()).run();
    }

    @Test
    public final void shouldRunAnnotatedMethodWithPjpAsArgument() throws Throwable {
        // given
        enablePlugin(PLUGIN_NAME);
        final ProceedingJoinPoint pjpMock = mock(ProceedingJoinPoint.class);
        final ClassWithRegularMethodExpectingPjpArgument object = new ClassWithRegularMethodExpectingPjpArgument();

        // when
        object.doSthg(pjpMock);

        // then
        verify(pjpMock).proceed();
    }

    @Test
    public final void shouldNotRunAnnotatedMethodWithPjpAsArgument() throws Throwable {
        // given
        final ProceedingJoinPoint pjpMock = mock(ProceedingJoinPoint.class);
        final ClassWithRegularMethodExpectingPjpArgument object = new ClassWithRegularMethodExpectingPjpArgument();

        // when
        object.doSthg(pjpMock);

        // then
        verify(pjpMock, never()).proceed();
    }

    @Test
    public final void shouldRunAnnotatedMethodWithPjpAsFirstOfManyArgument() throws Throwable {
        // given
        enablePlugin(PLUGIN_NAME);
        final ProceedingJoinPoint pjpMock = mock(ProceedingJoinPoint.class);
        final ClassWithRegularMethodExpectingPjpArgument object = new ClassWithRegularMethodExpectingPjpArgument();

        // when
        object.doSthgWithManyArgs(pjpMock, "some arbitrary second arg");

        // then
        verify(pjpMock).proceed();
    }

    @Test
    public final void shouldNotRunAnnotatedMethodWithPjpAsFirstOfManyArgument() throws Throwable {
        // given
        final ProceedingJoinPoint pjpMock = mock(ProceedingJoinPoint.class);
        final ClassWithRegularMethodExpectingPjpArgument object = new ClassWithRegularMethodExpectingPjpArgument();

        // when
        object.doSthgWithManyArgs(pjpMock, "some arbitrary second arg");

        // then
        verify(pjpMock, never()).proceed();
    }

    @Test
    public final void shouldRunAnnotatedAroundAdviceButPerformJoinPointExecution() {
        // given
        enablePlugin(PLUGIN_NAME);
        ClassWithoutAnnotations object = new ClassWithoutAnnotations(dependencyMock);

        // when
        object.runFirst();

        // then
        verify(dependencyMock).run();
        verify(aspectDependencyMock).runBefore();
        verify(aspectDependencyMock).runAround();
        verify(aspectDependencyMock).runAfter();
    }

    @Test
    public final void shouldNotRunAnnotatedAroundAdviceButPerformJoinPointExecution() {
        // given
        ClassWithoutAnnotations object = new ClassWithoutAnnotations(dependencyMock);

        // when
        object.runFirst();

        // then
        verify(dependencyMock).run();
        verify(aspectDependencyMock, never()).runBefore();
        verify(aspectDependencyMock, never()).runAround();
        verify(aspectDependencyMock, never()).runAfter();
    }

    @Test
    public final void shouldRunAnnotatedAroundAdviceWithManyArgumentsButPerformJoinPointExecution() {
        // given
        enablePlugin(PLUGIN_NAME);
        ClassWithoutAnnotations object = new ClassWithoutAnnotations(dependencyMock);

        // when
        object.runThird("someArg");

        // then
        verify(dependencyMock).run();
        verify(aspectDependencyMock).runAround();
    }

    @Test
    public final void shouldNotRunAnnotatedAroundAdviceWithManyArgumentsButPerformJoinPointExecution() {
        // given
        ClassWithoutAnnotations object = new ClassWithoutAnnotations(dependencyMock);

        // when
        object.runThird("someArg");

        // then
        verify(dependencyMock).run();
        verify(aspectDependencyMock, never()).runAround();
    }

    @Test
    public final void shouldRunAspectAnnotatedAroundAdviceButPerformJoinPointExecution() {
        // given
        enablePlugin(PLUGIN_NAME);
        ClassWithoutAnnotations object = new ClassWithoutAnnotations(dependencyMock);

        // when
        object.runSecond();

        // then
        verify(dependencyMock).run();
        verify(aspectDependencyMock).runBefore();
        verify(aspectDependencyMock).runAround();
        verify(aspectDependencyMock).runAfter();
    }

    @Test
    public final void shouldNotRunAspectAnnotatedAroundAdviceButPerformJoinPointExecution() {
        // given
        ClassWithoutAnnotations object = new ClassWithoutAnnotations(dependencyMock);

        // when
        object.runSecond();

        // then
        verify(dependencyMock).run();
        verify(aspectDependencyMock, never()).runBefore();
        verify(aspectDependencyMock, never()).runAround();
        verify(aspectDependencyMock, never()).runAfter();
    }

    @Test
    public final void shouldRunAspectAnnotatedAroundAdviceButNotPerformJoinPointExecution() {
        // given
        enablePlugin(PLUGIN_NAME);
        ClassWithoutAnnotations object = new ClassWithoutAnnotations(dependencyMock);

        // when
        object.runFourth();

        // then
        verify(dependencyMock, never()).run();
        verify(aspectDependencyMock).runAround();
    }

    @Test
    public final void shouldNotRunAspectAnnotatedAroundAdviceButPerformJoinPointExecutionEvenIfAdviceBodyDoseNotPerformThem() {
        // given
        ClassWithoutAnnotations object = new ClassWithoutAnnotations(dependencyMock);

        // when
        object.runFourth();

        // then
        verify(dependencyMock).run();
        verify(aspectDependencyMock, never()).runAround();
    }

    @Aspect
    public static final class AspectWithMethodLevelAnnotation {

        @RunIfEnabled(PLUGIN_NAME)
        @org.aspectj.lang.annotation.Before("execution(* ClassWithoutAnnotations.runFirst())")
        public void before() {
            aspectDependencyMock.runBefore();
        }

        @RunIfEnabled(PLUGIN_NAME)
        @Around("execution(* ClassWithoutAnnotations.runFirst())")
        public void around(final ProceedingJoinPoint pjp) throws Throwable {
            aspectDependencyMock.runAround();
            pjp.proceed();
        }

        @RunIfEnabled(PLUGIN_NAME)
        @After("execution(* ClassWithoutAnnotations.runFirst())")
        public void after() {
            aspectDependencyMock.runAfter();
        }

    }

    @Aspect
    @RunIfEnabled(PLUGIN_NAME)
    public static final class AspectWithClassLevelAnnotation {

        @org.aspectj.lang.annotation.Before("execution(* ClassWithoutAnnotations.runSecond())")
        public void before() {
            aspectDependencyMock.runBefore();
        }

        @Around("execution(* ClassWithoutAnnotations.runSecond())")
        public void around(final ProceedingJoinPoint pjp) throws Throwable {
            aspectDependencyMock.runAround();
            pjp.proceed();
        }

        @Around("execution(* ClassWithoutAnnotations.runThird(..)) && args(someArg)")
        public void aroundWithManyArgs(final ProceedingJoinPoint pjp, final Object someArg) throws Throwable {
            aspectDependencyMock.runAround();
            pjp.proceed();
        }

        @Around("execution(* ClassWithoutAnnotations.runFourth())")
        public void aroundWithoutPjpProceedInBody(final ProceedingJoinPoint pjp) throws Throwable {
            aspectDependencyMock.runAround();
        }

        @After("execution(* ClassWithoutAnnotations.runSecond())")
        public void after() {
            aspectDependencyMock.runAfter();
        }

    }

    @SuppressWarnings("deprecation")
    private void enablePlugin(final String pluginName) {
        given(pluginStateResolver.isEnabled(pluginName)).willReturn(true);
        given(pluginStateResolver.isEnabledOrEnabling(pluginName)).willReturn(true);
    }

}
