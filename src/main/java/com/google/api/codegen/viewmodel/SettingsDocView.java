/* Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.api.codegen.viewmodel;

import com.google.api.codegen.config.TransportProtocol;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class SettingsDocView {
  public abstract String serviceHostname();

  public abstract Integer servicePort();

  public abstract String exampleApiMethodName();

  public abstract String exampleApiMethodSettingsGetter();

  public abstract boolean exampleApiMethodSettingsHasRetrySettings();

  public abstract String apiClassName();

  public abstract String settingsVarName();

  public abstract String settingsClassName();

  public abstract String settingsBuilderVarName();

  public abstract boolean hasDefaultInstance();

  public abstract TransportProtocol transportProtocol();

  public static Builder newBuilder() {
    return new AutoValue_SettingsDocView.Builder().transportProtocol(TransportProtocol.GRPC);
  }

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder serviceHostname(String val);

    public abstract Builder servicePort(Integer val);

    public abstract Builder exampleApiMethodName(String val);

    public abstract Builder exampleApiMethodSettingsGetter(String val);

    public abstract Builder exampleApiMethodSettingsHasRetrySettings(boolean val);

    public abstract Builder apiClassName(String val);

    public abstract Builder settingsVarName(String val);

    public abstract Builder settingsClassName(String val);

    public abstract Builder settingsBuilderVarName(String val);

    public abstract Builder hasDefaultInstance(boolean hasDefaultInstance);

    public abstract Builder transportProtocol(TransportProtocol val);

    public abstract SettingsDocView build();
  }
}
