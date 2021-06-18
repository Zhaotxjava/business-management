/*
 * Project Name: eis-archetype
 * File Name: AppConfig.java
 * Class Name: AppConfig
 *
 * Copyright 2014 Hengtian Software Inc
 *
 * Licensed under the Hengtiansoft
 *
 * http://www.hengtiansoft.com
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hfi.insurance.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 获取应用全局配置
 *
 * @author minghan
 */
@Component
public final class AppConfigUtil {

    private static Environment envTemp;

    @Autowired
    private Environment env;

    private AppConfigUtil() {
    }

    @PostConstruct
    public void init() {
        envTemp = env;
    }

    private static String getProfile(String[] profiles) {
        if (profiles != null && profiles.length > 0) {
            return profiles[0];
        } else {
            return null;
        }
    }

    public static String getActiveProfile() {
        String profile = getProfile(envTemp.getActiveProfiles());
        if (StringUtils.isBlank(profile)) {
            profile = getProfile(envTemp.getDefaultProfiles());
            if (StringUtils.isBlank(profile)) {
                return "DEV";
            }
        }
        return profile;
    }

    public static boolean isProdEnv() {
        return "PROD".equalsIgnoreCase(getActiveProfile());
    }

    public static boolean isTestEnv() {
        return "TEST".equalsIgnoreCase(getActiveProfile());
    }

    public static boolean isDevEnv() {
        return "DEV".equalsIgnoreCase(getActiveProfile());
    }


}
