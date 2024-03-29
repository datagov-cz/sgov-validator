package com.github.sgov.server;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileUtils;
import org.topbraid.jenax.progress.NullProgressMonitor;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.rules.RuleUtil;
import org.topbraid.shacl.validation.ResourceValidationReport;
import org.topbraid.shacl.validation.ValidationReport;
import org.topbraid.shacl.validation.ValidationUtil;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("MissingJavadocType")
public class Validator {

    private final Set<URL> glossaryRules = new HashSet<>();
    private final Set<URL> modelRules = new HashSet<>();
    private final Set<URL> vocabularyRules = new HashSet<>();

    private final Model shapesModel;
    private final Model mappingModel;

    /**
     * Validator constructor.
     */
    public Validator() {
        for (int i = 1; i <= 14; i++) {
            glossaryRules.add(resource("g" + i));
        }
        for (int i = 1; i <= 7; i++) {
            modelRules.add(resource("m" + i));
        }
        for (int i = 1; i <= 3; i++) {
            vocabularyRules.add(resource("s" + i));
        }

        // inference rules
        shapesModel = ModelFactory.createDefaultModel();
        shapesModel.read(
            com.github.sgov.server.Validator.class.getResourceAsStream("/inference-rules.ttl"),
            null,
            FileUtils.langTurtle);

        // mapping Z-SGoV to UFO
        mappingModel = ModelFactory.createDefaultModel();
        mappingModel.read(
            com.github.sgov.server.Validator.class.getResourceAsStream("/z-sgov-mapping.ttl"),
            null,
            FileUtils.langTurtle);
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

    /**
     * Loads a model containing validation rules retrieved from URLs in the specified collection.
     *
     * @param rules URLs of validation rules
     * @return Jena model with the validation rules
     * @throws IOException When unable to load ony of the rules
     */
    public static Model getRulesModel(final Collection<URL> rules) throws IOException {
        final Model shapesModel = JenaUtil.createMemoryModel();
        for (URL r : rules) {
            shapesModel.read(r.openStream(), null, FileUtils.langTurtle);
        }
        return shapesModel;
    }

    /**
     * Validates the given model with vocabulary data (glossaries, models) against the given ruleset and inference
     * rules.
     *
     * @param dataModel model with data to validate
     * @param ruleSet   set of rules (see 'resources') used for validation
     * @return validation report
     */
    public ValidationReport validate(final Model dataModel, final Set<URL> ruleSet)
        throws IOException {
        final Model shapesModel = getRulesModel(ruleSet);
        return validate(dataModel, shapesModel);
    }

    /**
     * Validates the given model with vocabulary data (glossaries, models) against the given shapes model containing
     * validation rules and the default inference rules provided by this validator.
     *
     * @param dataModel   model with data to validate
     * @param shapesModel model with validation rules
     * @return validation report
     */
    public ValidationReport validate(final Model dataModel, final Model shapesModel) {
        shapesModel.add(this.shapesModel);

        dataModel.add(mappingModel);

        final Model inferredModel = RuleUtil.executeRules(dataModel, shapesModel, null,
                                                          new NullProgressMonitor());
        dataModel.add(inferredModel);

        final Resource report = ValidationUtil.validateModel(dataModel, shapesModel, true);

        return new ResourceValidationReport(report);
    }
}
