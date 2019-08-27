/*
 * Copyright © 2019 no and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.example.impl;

import java.util.concurrent.Future;

import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.example.rev190827.ExampleService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.example.rev190827.ExampleInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.example.rev190827.ExampleOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.example.rev190827.ExampleOutputBuilder;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;


public class ExampleImpl implements ExampleService {

    @Override
    public Future<RpcResult<ExampleOutput>> exampleWorld(ExampleInput input) {
        ExampleOutputBuilder exampleBuilder = new ExampleOutputBuilder();
        exampleBuilder.setGreeting("Example " + input.getName());
        return RpcResultBuilder.success(exampleBuilder.build()).buildFuture();
    }
}