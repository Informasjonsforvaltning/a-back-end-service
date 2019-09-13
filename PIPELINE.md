Prinsipp:
* Vi implementerer GitHub flow. Det medfører at "__Anything in the master branch is deployable__"

CI: bygg + alle tester (unit, integration, service) + sonarrapport + eventuelle sonar tester

Pipeline-forslag
1. Ved opprettelse av PR av feature-branch til master, køres CI på feature-branch.
2. Når feature-branch har fått aksept frå reviewers, trigges deploy av feature-branch artefakt til staging.
3. Deploy av feature-branch til staging trigger godjenning.
4. Når testleder godkjenner, trigges merge av feature til master, som deployes til prod.
