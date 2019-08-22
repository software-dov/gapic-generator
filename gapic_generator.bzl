# Copyright 2019 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.


def gapic_generator_tests(name, srcs, runtime_deps, size):
    classNames = []
    for src in srcs:
        # convert .java file path to fully qualified class name
        className = src[(src.index("/com/") + 1):-5].replace("/", ".")
        classNames.append(className)
        native.java_test(
            name=className,
            test_class=className,
            runtime_deps=runtime_deps,
            size=size,
        )
    if classNames:
        native.test_suite(
            name=name,
            tests=classNames,
        )


def google_java_format(name, srcs, formatter):
    native.genrule(
        name=name,
        outs=["%s.sh" % name],
        srcs=srcs,
        # TODO: this may fail if list of files is too long (exceeds max command line limit in shell).
        #       Split the command into multiple executions if this ever fails (good enough for now)
        cmd="echo ' $(location %s) --replace $(SRCS)' > $@" % formatter,
        executable=True,
        tools=[formatter],
        local=1,
    )


def _google_java_format_verification_impl(ctx):
    src_files = [src.path for src in ctx.files.srcs]
    output_file = ctx.outputs.output_file
    formatter = ctx.executable.formatter

    ctx.actions.run_shell(
        inputs=ctx.files.srcs,
        arguments=["--dry-run", "--set-exit-if-changed"] + src_files,
        tools=[formatter],
        command="%s $@ > %s" % (formatter.path, output_file.path),
        outputs=[output_file],
        progress_message="If this target fails check the list of files that must be formatted in %s" % output_file.path,
    )


google_java_format_verification = rule(
    attrs={
        "srcs": attr.label_list(allow_files=True),
        "formatter": attr.label(
            executable=True,
            cfg="host",
        ),
    },
    outputs={"output_file": "%{name}.txt"},
    implementation=_google_java_format_verification_impl,
)

#
# Samplegen stuff
#


def _samplegen_custom_library_impl(ctx):
    args = ["--samples=%s" % s for s in ctx.attr.sample_configs]
    args.extend(["--language", ctx.attr.language])
    ctx.actions.run(
        inputs=ctx.files.sample_configs,
        outputs=["samples"],
        executable=ctx.executable.gapic_generator,
        arguments=args,
        progress_message="",  # TODO: flesh this output
    )


samplegen = rule(
    attrs={
        "service_proto_library": attr.label(),
        "sample_configs": attr.label_list(),
        "_protoc": attr.label(
            default=Label("@com_google_protobuf//:protoc"),
            executable=True,
            cfg="host",
        ),
        "gapic_generator": attr.label(
            default=Label("//:gapic_generator"),
            executable=True,
            cfg="host",
        ),
        "language": attr.string(mandatory=True),
    },
    outputs={
        "output": "samples"
    },
    implementation=_samplegen_custom_library_impl,
)


# Also need the serialized proto
def java_generated_samples(name, service_proto_library, sample_configs):
    samplegen(
        name=name,
        service_proto_library=service_proto_library,
        sample_configs=sample_configs,
        language="java",
    )
