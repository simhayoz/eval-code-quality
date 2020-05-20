# Evaluating code quality

[![Build Status](https://travis-ci.org/simhayoz/eval-code-quality.svg?branch=master)](https://travis-ci.org/simhayoz/eval-code-quality)
[![Maintainability](https://api.codeclimate.com/v1/badges/a13efbdaea1fe65d9120/maintainability)](https://codeclimate.com/github/simhayoz/eval-code-quality/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/a13efbdaea1fe65d9120/test_coverage)](https://codeclimate.com/github/simhayoz/eval-code-quality/test_coverage)
> Work in progress

## Checks
Currently this app can do the following checks:
- [BlankLines](https://github.com/simhayoz/eval-code-quality/wiki/Blank-lines-check): checks that there is not more than one blank line in a row
- [Braces](https://github.com/simhayoz/eval-code-quality/wiki/Braces-check): do the following checks:
  - checks that braces are either aligned with parent or on the following line
  - checks that every parent - opening braces respects the same style (i.e. all with braces on the same line or all with braces on the following line)
    - Do the same for the child - previous block closing braces and child - opening braces
  - checks that one liner block either all have braces or none have them
- [DesignPattern](https://github.com/simhayoz/eval-code-quality/wiki/Design-pattern-check):
  - [SingletonPattern](https://github.com/simhayoz/eval-code-quality/wiki/Singleton-pattern-check): checks that a certain class is a Singleton Pattern
  - [BuilderPattern](https://github.com/simhayoz/eval-code-quality/wiki/Builder-pattern-check): checks that a certain `Product` class and `Builder` class form a Builder Pattern
  - [VisitorPattern](https://github.com/simhayoz/eval-code-quality/wiki/Visitor-pattern-check): checks that a certain `Visitor` class, `Parent` class and multiple `Children` classes form a Visitor Pattern
- [Indentation](https://github.com/simhayoz/eval-code-quality/wiki/Indentation-check): checks that every element within a block is aligned, that every block has the same tab difference from parent and that this difference is positive
- [Naming](https://github.com/simhayoz/eval-code-quality/wiki/Naming-check): checks that for the same modifiers (public, static, etc) the name of the variables and methods respects the same convention
