# CRUD Users

[![Build Status](https://travis-ci.org/ssuarez6/crudusers.svg?branch=develop)](https://travis-ci.org/ssuarez6/crudusers) [![Coverage Status](https://coveralls.io/repos/github/ssuarez6/crudusers/badge.svg?branch=develop)](https://coveralls.io/github/ssuarez6/crudusers?branch=master) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/1e0843fe00af44f9a465433c64db6a72)](https://www.codacy.com/app/ssuarez6/crudusers?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=ssuarez6/crudusers&amp;utm_campaign=Badge_Grade) [![Dependencies](https://app.updateimpact.com/badge/844565191365627904/Tests.svg?config=compile)](https://app.updateimpact.com/latest/844565191365627904/Tests)

This repo is intended to test akka-http with Cassandra and message-handling with Kafka

## Run

You have to check that you have zookeeper server running and kafka server running at port **9092**.
Then, open two terminals to run first **sbt run** and selecting option 1, and then again **sbt run** and select option 2 for running the message consumer.
