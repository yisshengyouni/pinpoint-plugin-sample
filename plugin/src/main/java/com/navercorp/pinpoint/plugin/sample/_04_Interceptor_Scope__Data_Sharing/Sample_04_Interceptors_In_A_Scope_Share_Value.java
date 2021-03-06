/**
 * Copyright 2014 NAVER Corp.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.plugin.sample._04_Interceptor_Scope__Data_Sharing;

import java.security.ProtectionDomain;

import com.navercorp.pinpoint.bootstrap.instrument.InstrumentClass;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentException;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentMethod;
import com.navercorp.pinpoint.bootstrap.instrument.Instrumentor;
import com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformCallback;
import com.navercorp.pinpoint.bootstrap.interceptor.scope.ExecutionPolicy;
import com.navercorp.pinpoint.bootstrap.interceptor.scope.InterceptorScope;

/*
 * Sometimes interceptors need to share values to trace a transaction.
 * You can use InterceptorScope for that purpose.
 */
public class Sample_04_Interceptors_In_A_Scope_Share_Value implements TransformCallback {

    @Override
    public byte[] doInTransform(Instrumentor instrumentor, ClassLoader classLoader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws InstrumentException {
        InstrumentClass target = instrumentor.getInstrumentClass(classLoader, className, classfileBuffer);
        
        // Get the scope object from Instrumentor
        InterceptorScope scope = instrumentor.getInterceptorScope("SAMPLE_04_SCOPE");

        // Put interceptors need to share data to a same scope.
        InstrumentMethod outerMethod = target.getDeclaredMethod("outerMethod", "java.lang.String");
        outerMethod.addScopedInterceptor("com.navercorp.pinpoint.plugin.sample._04_Interceptor_Scope__Data_Sharing.OuterMethodInterceptor", scope);
        
        // Note that execution policy of InnerMethodInterceptor is INTERNAL to make the interceptor runs only when other interceptor in the scope is active.
        InstrumentMethod innerMethod = target.getDeclaredMethod("innerMethod", "java.lang.String");
        innerMethod.addScopedInterceptor("com.navercorp.pinpoint.plugin.sample._04_Interceptor_Scope__Data_Sharing.InnerMethodInterceptor", scope, ExecutionPolicy.INTERNAL);
        
        return target.toBytecode();
    }

}
