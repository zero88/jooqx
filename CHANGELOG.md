# Changelog

## [v2.0.0-rc1](https://github.com/zero88/jooqx/tree/v2.0.0-rc1) (2022-08-23)

[Full Changelog](https://github.com/zero88/jooqx/compare/v1.0.0...v2.0.0-rc1)

**Breaking changes:**

- Move SQLResultAdapter is close to RowDecoder  [\#115](https://github.com/zero88/jooqx/issues/115)
- Breaking change to jooqx v2 [\#101](https://github.com/zero88/jooqx/issues/101)
- JSON serialization SPI [\#12](https://github.com/zero88/jooqx/issues/12)

**Implemented enhancements:**

- Able to execute multiple statements in one connection [\#137](https://github.com/zero88/jooqx/issues/137)
- Add shortcut for fetchJsonObject/fetchJsonArray [\#133](https://github.com/zero88/jooqx/issues/133)
- Add shortcut for fetchOne/fetchMany/fetchExists/fetchCount into jooqx [\#129](https://github.com/zero88/jooqx/issues/129)
- Refactor code generation for `rxify` [\#103](https://github.com/zero88/jooqx/issues/103)
- Support SQL block [\#98](https://github.com/zero88/jooqx/issues/98)
- Support plain SQL [\#97](https://github.com/zero88/jooqx/issues/97)
- Support mutiny [\#83](https://github.com/zero88/jooqx/issues/83)
- Support Rx3 [\#82](https://github.com/zero88/jooqx/issues/82)
- Remove lombok dependency [\#65](https://github.com/zero88/jooqx/issues/65)
- Support DDL [\#51](https://github.com/zero88/jooqx/issues/51)
- Introduce jooqx SPI provider [\#49](https://github.com/zero88/jooqx/issues/49)
- Rxify for JooqxBuilder [\#38](https://github.com/zero88/jooqx/issues/38)
- Support plain JDBC in Vertx reactive mode [\#33](https://github.com/zero88/jooqx/issues/33)
- Support SQL Procedure [\#25](https://github.com/zero88/jooqx/issues/25)
- Integrate with RQL [\#8](https://github.com/zero88/jooqx/issues/8)

**Fixed bugs:**

- MySQL select count [\#124](https://github.com/zero88/jooqx/issues/124)
- Unable convert data by Select [\#107](https://github.com/zero88/jooqx/issues/107)
- Wrong jooqx-core-test-fixtures on Nexus [\#53](https://github.com/zero88/jooqx/issues/53)

**Merged pull requests:**

- Implement JooqxSession [\#138](https://github.com/zero88/jooqx/pull/138) ([zero88](https://github.com/zero88))
- Feature/support block sql statement [\#135](https://github.com/zero88/jooqx/pull/135) ([zero88](https://github.com/zero88))
- Feature/add shortcut for fetch json [\#134](https://github.com/zero88/jooqx/pull/134) ([zero88](https://github.com/zero88))
- Feature/introduce collector by sql result adapter [\#131](https://github.com/zero88/jooqx/pull/131) ([zero88](https://github.com/zero88))
- Feature/refactor sql result adapter [\#128](https://github.com/zero88/jooqx/pull/128) ([zero88](https://github.com/zero88))
- Feature/procedure [\#123](https://github.com/zero88/jooqx/pull/123) ([zero88](https://github.com/zero88))
- Improvement/stablize test [\#122](https://github.com/zero88/jooqx/pull/122) ([zero88](https://github.com/zero88))
- Feature/add reactivex generator [\#113](https://github.com/zero88/jooqx/pull/113) ([zero88](https://github.com/zero88))
- breaking-change-to-v2 [\#102](https://github.com/zero88/jooqx/pull/102) ([zero88](https://github.com/zero88))
- Support plain SQL [\#100](https://github.com/zero88/jooqx/pull/100) ([zero88](https://github.com/zero88))
- Feature/add rxify builder [\#95](https://github.com/zero88/jooqx/pull/95) ([zero88](https://github.com/zero88))
- Feature/remove lombok [\#92](https://github.com/zero88/jooqx/pull/92) ([zero88](https://github.com/zero88))
- Feature/add documentation branch [\#86](https://github.com/zero88/jooqx/pull/86) ([zero88](https://github.com/zero88))
- Remove lombok in jpa-ext [\#84](https://github.com/zero88/jooqx/pull/84) ([zero88](https://github.com/zero88))
- Feature/remove lombok in rsql [\#68](https://github.com/zero88/jooqx/pull/68) ([zero88](https://github.com/zero88))
- Feature/upgrade project deps [\#64](https://github.com/zero88/jooqx/pull/64) ([zero88](https://github.com/zero88))
- Feature/merge with rsql project [\#62](https://github.com/zero88/jooqx/pull/62) ([zero88](https://github.com/zero88))
- Feature/support ddl [\#52](https://github.com/zero88/jooqx/pull/52) ([zero88](https://github.com/zero88))
- Feature/spi provider [\#50](https://github.com/zero88/jooqx/pull/50) ([zero88](https://github.com/zero88))

## [1.0.0](https://github.com/zero88/jooqx/tree/v1.0.0)

[Full Changelog](https://github.com/zero88/jooqx/compare/f7e4efadba4209f4b39548e08bf60ba814e4c6bb...HEAD)

**Closed issues:**

- Document & usage [\#24](https://github.com/zero88/jooqx/issues/24)
- Vertx gen to Rxify [\#18](https://github.com/zero88/jooqx/issues/18)
- Test with complex query that return join result to ensure VertxJooqRecord can handle [\#11](https://github.com/zero88/jooqx/issues/11)
- Add some useful basic SQL query as DAO function [\#10](https://github.com/zero88/jooqx/issues/10)
- Convert result from VertxJooqRecord to Table record or Table pojo [\#9](https://github.com/zero88/jooqx/issues/9)
- Support SQL transaction [\#7](https://github.com/zero88/jooqx/issues/7)
- Data type converter between Vertx SQL and jOOQ [\#6](https://github.com/zero88/jooqx/issues/6)
- Error handling [\#5](https://github.com/zero88/jooqx/issues/5)
- Support batch DML [\#3](https://github.com/zero88/jooqx/issues/3)

**Merged pull requests:**

- Feature/rxify v2 [\#34](https://github.com/zero88/jooqx/pull/34) ([zero88](https://github.com/zero88))
- Vert.x jOOQ Data converter [\#32](https://github.com/zero88/jooqx/pull/32) ([zero88](https://github.com/zero88))
- Improve/api-2-document [\#30](https://github.com/zero88/jooqx/pull/30) ([zero88](https://github.com/zero88))
- Feature/docs [\#28](https://github.com/zero88/jooqx/pull/28) ([zero88](https://github.com/zero88))
- Feature/transaction [\#21](https://github.com/zero88/jooqx/pull/21) ([zero88](https://github.com/zero88))
- Feature/complex query [\#19](https://github.com/zero88/jooqx/pull/19) ([zero88](https://github.com/zero88))
- Feature/common dsl [\#17](https://github.com/zero88/jooqx/pull/17) ([zero88](https://github.com/zero88))
- Feature/error handling [\#16](https://github.com/zero88/jooqx/pull/16) ([zero88](https://github.com/zero88))
- Feature/support batch [\#15](https://github.com/zero88/jooqx/pull/15) ([zero88](https://github.com/zero88))
- Bump actions/cache from v2 to v2.1.4 [\#14](https://github.com/zero88/jooqx/pull/14) ([dependabot[bot]](https://github.com/apps/dependabot))
- Bump zero88/gh-project-context from v1.0.0 to v1.0.3 [\#2](https://github.com/zero88/jooqx/pull/2) ([dependabot[bot]](https://github.com/apps/dependabot))
- Bump actions/cache from v2.1.3 to v2.1.4 [\#1](https://github.com/zero88/jooqx/pull/1) ([dependabot[bot]](https://github.com/apps/dependabot))



\* *This Changelog was automatically generated by [github_changelog_generator](https://github.com/github-changelog-generator/github-changelog-generator)*
