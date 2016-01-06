package io.gapi.vgen;

import com.google.api.tools.framework.tools.ToolOptions;

import java.util.ArrayList;
import java.util.List;

// TODO(jgeiger): inherit from GoogleToolDriverBase or the open-source equivalent once available
// to use ToolOptions instead of parsing the args ourselves
//
// Example usage: (assuming environment variable BASE is the base directory of the project
// containing the yamls, descriptor set, and output)
//
//     CodeGeneratorTool --descriptorSet=$BASE/src/main/generated/_descriptors/bigtable.desc \
//        --serviceYaml=$BASE/src/main/configs/bigtabletableadmin.yaml \
//        --veneerYaml=$BASE/src/main/configs/bigtable_table_veneer.yaml \
//        --output=$BASE
public class CodeGeneratorTool {

  static String outputDirectory = "";
  static String descriptorSet = "";
  static List<String> serviceYaml = new ArrayList<>();
  static List<String> veneerYaml = new ArrayList<>();

  public static void main(String[] args) {
    parseArgs(args);
    generate(descriptorSet, serviceYaml, veneerYaml, outputDirectory);
  }

  private static void parseArgs(String[] args) {
    String missingArgMsg = "Command-line argument '%s' must be followed by an argument";

    List<String> argList = new ArrayList<>();
    for (String arg : args) {
      String[] eqs = arg.split("=", 2);
      argList.add(eqs[0]);
      if (eqs.length > 1) {
        String[] params = eqs[1].split(" ");
        for (String param : params) {
          argList.add(param);
        }
      }
    }

    for (int i = 0; i < argList.size(); i++) {
      if(argList.get(i).equals("--output")) {
        assertOrDie(++i < argList.size(), String.format(missingArgMsg, "--output"));
        outputDirectory = argList.get(i);

      } else if(argList.get(i).equals("--descriptorSet")) {
        assertOrDie(++i < argList.size(), String.format(missingArgMsg, "--descriptorSet"));
        descriptorSet = argList.get(i);

      } else if(argList.get(i).equals("--serviceYaml")) {
        assertOrDie(++i < argList.size(), String.format(missingArgMsg, "--serviceYaml"));
        serviceYaml.add(argList.get(i));
        while (++i < args.length && argList.get(i).charAt(0) != '-') {
          serviceYaml.add(argList.get(i));
        }
        i--;

      } else if(argList.get(i).equals("--veneerYaml")) {
        assertOrDie(++i < argList.size(), String.format(missingArgMsg, "--veneerYaml"));
        veneerYaml.add(argList.get(i));
        while (++i < args.length && argList.get(i).charAt(0) != '-') {
          veneerYaml.add(argList.get(i));
        }
        i--;

      } else {
        System.err.println("Unexpected option: " + argList.get(i));
        System.err.println("Usage: CodeGeneratorTool [--descriptorSet=D] "
            + "[--serviceYaml=S ... ] [--veneerYaml=V ...] [--output=O]");
        System.exit(1);
      }
    }
  }

  private static void assertOrDie (boolean cond, String msg) {
    if (!cond) {
      System.err.println(msg);
      System.exit(1);
    }
  }

  private static void generate(String descriptorSet, List<String> apiConfigs,
      List<String> generatorConfigs, String outputDirectory) {

    ToolOptions options = ToolOptions.create();
    options.set(ToolOptions.DESCRIPTOR_SET, descriptorSet);
    List<String> configs = new ArrayList<String>();
    for (String config : apiConfigs) {
      configs.add(config);
    }
    options.set(ToolOptions.CONFIG_FILES, configs);
    options.set(CodeGeneratorApi.OUTPUT_FILE, outputDirectory);
    List<String> genConfigs = new ArrayList<String>();
    for (String genConfig : generatorConfigs) {
      genConfigs.add(genConfig);
    }
    options.set(CodeGeneratorApi.GENERATOR_CONFIG_FILES, genConfigs);
    CodeGeneratorApi codeGen = new CodeGeneratorApi(options);
    codeGen.run();
  }
}


