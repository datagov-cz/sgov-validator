package com.github.sgov.server;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileUtils;
import org.topbraid.jenax.progress.SimpleProgressMonitor;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.rules.RuleUtil;
import org.topbraid.shacl.validation.ResourceValidationReport;
import org.topbraid.shacl.validation.ValidationReport;
import org.topbraid.shacl.validation.ValidationUtil;

@Slf4j
@SuppressWarnings("MissingJavadocType")
public class Validator {

    private final Set<URL> glossaryRules = new HashSet<>();
    private final Set<URL> modelRules = new HashSet<>();
    private final Set<URL> vocabularyRules = new HashSet<>();

    /**
     * Validator constructor.
     */
    public Validator() {
        for (int i = 1; i <= 10; i++) {
            glossaryRules.add(resource("g" + i));
        }
        for (int i = 1; i <= 7; i++) {
            modelRules.add(resource("m" + i));
        }
        for (int i = 1; i <= 1; i++) {
            vocabularyRules.add(resource("s" + i));
        }
    }

    private URL resource(final String name) {
        return getClass().getResource("/rules/" + name + ".ttl");
    }

    public Set<URL> getModelRules() {
        return modelRules;
    }

    public Set<URL> getGlossaryRules() {
        return glossaryRules;
    }

    public Set<URL> getVocabularyRules() {
        return vocabularyRules;
    }

    private Model getRulesModel(final Collection<URL> rules) throws IOException {
        final Model shapesModel = JenaUtil.createMemoryModel();
        for (URL r : rules) {
            shapesModel
                .read(r.openStream(), null, FileUtils.langTurtle);
        }
        return shapesModel;
    }

    /**
     * Validates the given model with vocabulary data (glossaries, models) against the given
     * ruleset.
     *
     * @param dataModel model with data to validate
     * @param ruleSet   set of rules (see 'resources') used for validation
     * @return validation report
     */
    public ValidationReport validate(final Model dataModel, final Set<URL> ruleSet)
        throws IOException {
        log.info("Validating model of size {}", dataModel.size());
        final Model shapesModel;
        shapesModel = getRulesModel(ruleSet);

        shapesModel.read(
            com.github.sgov.server.Validator.class.getResourceAsStream("/inference-rules.ttl"),
            null,
            FileUtils.langTurtle);

        final Model inferredModel = RuleUtil
            .executeRules(dataModel, shapesModel, null,
                new SimpleProgressMonitor("inference"));
        dataModel.add(inferredModel);

        final Resource report = ValidationUtil.validateModel(dataModel, shapesModel, true);

        return new ResourceValidationReport(report);
    }
}
