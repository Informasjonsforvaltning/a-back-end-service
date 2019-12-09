Prinsipp:
* Vi implementerer GitHub flow. Det medfører at "__Anything in the master branch is deployable__"

CI: bygg + alle tester (unit, integration, service) + sonarrapport + eventuelle sonar tester

Pipeline-forslag
1. Ved push til feature-branch, kjøres CI på feature-branch.
2. Ved opprettelse av PR av feature-branch til master, kjøres CI på feature-branch og deploy til staging (it1). (Inkludert laste jacoco-rapport opp til sonarcloud.io. Dvs køyre ./buildWithSonarReport.sh PS! Det må legges en nøkkel et sted.)
3. Codereview og UAT
4. Ved merge (= push to master) til master, CI på master og deploy til produksjon/demo

Dersom feil sniker seg inn i produksjon, må vi reverte til siste "friske" commit og køyre (4) på nytt.
