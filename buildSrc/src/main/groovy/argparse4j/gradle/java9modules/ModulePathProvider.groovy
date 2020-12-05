package argparse4j.gradle.java9modules

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.process.CommandLineArgumentProvider

class ModulePathProvider implements CommandLineArgumentProvider {
    private Project project
    private Configuration configuration

    ModulePathProvider(Project project, Configuration configuration) {
        this.project = project
        this.configuration = configuration
    }

    @Override
    Iterable<String> asArguments() {
        def modulePathConfiguration = project.configurations.create("modulePath" + new Random().nextLong())
        modulePathConfiguration.extendsFrom configuration
        modulePathConfiguration.transitive = false
        return Arrays.asList("--module-path", modulePathConfiguration.asPath)
    }
}
