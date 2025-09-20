Implemented basic methods of how a connection pool should look like.

Features:
1. Init DB connections with creds.
2. Using h2 in mem DB for this prototype.
3. Keeping usedConns, availableConns and passing on connections among these.
4. Added basic validations when the connections exceed.
5. Running it in an iterative fashion to execute the methods and cover most branches.
