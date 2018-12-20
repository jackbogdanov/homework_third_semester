/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sample;

import org.openjdk.jmh.annotations.*;
import org.sample.program.*;


import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class MyBenchmark {

    @State(Scope.Thread)
    public static class State1 {

        @Param({"systemCollection", "myCollection"})
        private String mode;

        @Param({"1", "2", "4", "8"})
        private int threadsCount;

        @Param({"0", "1"})
        private int depth;

        private final String URL = "http://www.mkyong.com/";
        public Thread crawler;

        @Setup(Level.Invocation)
        public void prepare() {

            switch (mode) {
                case "myCollection":
                    crawler = new Thread(new WEB_crawler_my(URL, depth, threadsCount));
                    break;
                default:
                    crawler = new Thread(new WEB_crawler_sys(URL, depth, threadsCount));
                    break;
            }

        }
        
    }

    @Benchmark
    @Warmup(iterations = 1)
    @Fork(1)
    @Measurement(iterations = 2)
    public void test(State1 st) {

        st.crawler.start();
        try {
            st.crawler.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}