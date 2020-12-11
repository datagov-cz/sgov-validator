# SGoV Validator

This repository contains validation rules for SGoV (it was delimited out of https://github.com/opendata-mvcr/sgov). It contains SHACL rules and a simple wrapper to evaluate them over a Jena model.

## SGoV Validation
The project contains a standalone validator for validating Semantic Government Vocabulary (SGoV). It includes checking consistency and compliance of glossaries and models according to predefined rules. These rules check:
- glossaries - e.g. "each glossary concept at least one skos:prefLabel"
- models - e.g. OntoUML relationships like "each Role concept must (transitively) inherit from a Kind concept"
- interplay between glossaries and models - e.g. "each glossary concept should be used in the model"

## Proposing validation changes
The best way to propose validation rule changes is to:

1. create a new issue using template 'Validation Change Request' template. Describe (i) why a you request to change validation (to add a new rule/change an existing one),
(ii) describe use-case/example. The issue is given a number <ISSUE>
2. create a new branch of the form '<ISSUE>-<short-description>'
3. implement the changes and create a pull request. If the pull request passes all automatic checks, ask one of the maintainers to approve.
4. Once approved, merge PR into master

## Running the test cases

    gradle test

### Intellij Idea

To manage source code it is recommended to install plugins:
 - [CheckStyle](https://plugins.jetbrains.com/plugin/1065-checkstyle-idea)
 
Static code analysis rules are defined in file `./config/checkstyle/checkstyle.xml`. In order to set up this checkstyle
in Intellij Idea IDE following steps are recommended:
1) Import the file into project checkstyle scheme using 
`Settings/Editor/Code Style/Java/Import Scheme/Checkstyle configuration`.
2) Install checkstyle plugin CheckStyle-IDEA
3) Configure the plugin by adding `checkstyle.xml` into  `Settings/Other Settings/Checkstyle/Configuration file`
-----
Tento repozitář vznikl v rámci projektu OPZ č. [CZ.03.4.74/0.0/0.0/15_025/0013983](https://esf2014.esfcr.cz/PublicPortal/Views/Projekty/Public/ProjektDetailPublicPage.aspx?action=get&datovySkladId=F5E162B2-15EC-4BBE-9ABD-066388F3D412).
![Evropská unie - Evropský sociální fond - Operační program Zaměstnanost](https://data.gov.cz/images/ozp_logo_cz.jpg)
