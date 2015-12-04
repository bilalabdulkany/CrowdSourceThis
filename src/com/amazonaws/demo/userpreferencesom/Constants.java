/*
 * Copyright 2010-2012 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 * 
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.amazonaws.demo.userpreferencesom;

import java.util.Random;

public class Constants {

    public static final String ACCOUNT_ID = "745327864203";
    public static final String IDENTITY_POOL_ID = "us-east-1:71a334e2-e0aa-447f-8c9b-f62357ce904a";
    public static final String UNAUTH_ROLE_ARN = "arn:aws:iam::745327864203:role/Cognito_test_CrowdSourceThisUnauth_Role";
    // Note that spaces are not allowed in the table name
    public static final String TEST_TABLE_NAME = "test_AWS_Table";

    public static final Random random = new Random();
    public static final String[] NAMES = new String[] {
             "Trevor", "Jeremy",
            "Ryan", "Matty", "Steve", "Pavel"
    };

    public static String getRandomName() {
        int name = random.nextInt(NAMES.length);

        return NAMES[name];
    }

    public static int getRandomScore() {
        return random.nextInt(1000) + 1;
    }
}
