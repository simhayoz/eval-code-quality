[![Build Status](https://travis-ci.org/simhayoz/eval-code-quality.svg?branch=master)](https://travis-ci.org/simhayoz/eval-code-quality)
[![Maintainability](https://api.codeclimate.com/v1/badges/a13efbdaea1fe65d9120/maintainability)](https://codeclimate.com/github/simhayoz/eval-code-quality/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/a13efbdaea1fe65d9120/test_coverage)](https://codeclimate.com/github/simhayoz/eval-code-quality/test_coverage)


# Evaluating code quality

The goal of this project was to create an automatic tool for checking code quality. 

The report PDF can be found [here](https://github.com/simhayoz/eval-code-quality/tree/master/report/Bachelor_project_report.pdf).

Bachelor project at [Dependable Systems Lab](https://dslab.epfl.ch/) at EPFL.

## Checks
Currently this app can do the following checks:
- [BlankLines](https://simhayoz.github.io/Bachelor_project_report.pdf#page=3): checks that there is not more than one blank line in a row
- [Braces](https://simhayoz.github.io/Bachelor_project_report.pdf#page=3): do the following checks:
  - checks that braces are either aligned with parent or on the following line
  - checks that every parent - opening braces respects the same style (i.e. all with braces on the same line or all with braces on the following line)
    - Do the same for the child - previous block closing braces and child - opening braces
  - checks that one liner block either all have braces or none have them
- [DesignPattern](https://simhayoz.github.io/Bachelor_project_report.pdf#page=5):
  - [SingletonPattern](https://simhayoz.github.io/Bachelor_project_report.pdf#page=5): checks that a certain class is a Singleton Pattern
  - [BuilderPattern](https://simhayoz.github.io/Bachelor_project_report.pdf#page=6): checks that a certain `Product` class and `Builder` class form a Builder Pattern
  - [VisitorPattern](https://simhayoz.github.io/Bachelor_project_report.pdf#page=6): checks that a certain `Visitor` class, `Parent` class and multiple `Children` classes form a Visitor Pattern
- [Indentation](https://simhayoz.github.io/Bachelor_project_report.pdf#page=4): checks that every element within a block is aligned, that every block has the same tab difference from parent and that this difference is positive
- [Naming](https://simhayoz.github.io/Bachelor_project_report.pdf#page=5): checks that for the same modifiers (public, static, etc) the name of the variables and methods respects the same convention
