# Job Title Normaliser

## Summary

This project implements a **Job Title Normaliser** that maps free-text job titles to a set of standardised (normalised) job titles using text similarity algorithms.

The application is written in **Java**, follows an **object-oriented design**, and demonstrates clean architecture principles, modularity, and defensive coding practices.

The normalisation approach combines **cosine similarity** and **fuzzy-token matching** to determine the best match for an input job title, returning the normalised version if the similarity exceeds a defined threshold. The implementation includes unit testing, configuration management, and documentation.

### Example

| Input | Output |
|-------|--------|
| `Java engineer` | `Software engineer` |
| `C# engineer` | `Software engineer` |
| `Accountant` | `Accountant` |
| `Chief Accountant` | `Accountant` |

---

## Assumptions

* It is out of scope to match titles with the same semantic meaning but completely different letters (natural language processing). E.g., “developer” and “engineer”. The task timeframe does not allow for a large amount of effort. This could be implemented using alternate names and primary names.
* If there’s no match, it’s OK to return an empty string.
* It’s OK to add more job titles to the given table.
* The class does not need to cover an extensive set of use cases (in real world data can be noisy). This requires too much effort for given timeframe.


## Running the project

Clone the repository:

```bash
git clone https://github.com/Zzanetiite/job-title-normaliser
```

Run the project from chosen IDE.

## Resources

- [Apache Commons Text](https://commons.apache.org/proper/commons-text/) – Jaro–Winkler similarity for fuzzy string matching
- [Gradle](https://gradle.org/) – Build automation and dependency management for Java projects
- [JUnit 5](https://junit.org/junit5/) – Unit testing framework for Java
- [Java Stream API](https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html) – Used for token filtering, mapping, and processing
- [Cosine Similarity (Wikipedia)](https://en.wikipedia.org/wiki/Cosine_similarity) – Explanation of cosine similarity for vector comparisons
- [Fuzzy Matching Overview (Medium)](https://medium.com/@ievgenii.shulitskyi/string-data-normalization-and-similarity-matching-algorithms-4b7b1734798e) – General background on string normalisation and similarity algorithms
