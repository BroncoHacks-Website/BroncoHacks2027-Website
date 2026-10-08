# Frontend Data Flow 
as of OCT 4

## Current flows

| Flow | Current frontend behavior | Backend integration |
|---|---|---|
| Authentication | `lib/auth-client.ts` returns mock sign-in/sign-up results. `isAuthenticated()` always returns `false` with no backend session. |  `POST /auth/signin`. handle sign-in route in \backend. No sign-up route or session/token contract is available yet. |
| Team list | frontend `app/teams/page.tsx` renders `mockTeams`; localStorage can override member counts. Search and sorting are client-side. | Load teams list details from `GET /api/teams`. |
| Team details | `app/teams/[teamId]/page.tsx` uses another `mockTeams` array with hard-coded current user. Membership is stored in localStorage. | Load a team from `GET /api/teams` and select by ID; no detail endpoint exists. Use the authenticated identity and `POST /api/teams/join` or `POST /api/teams/leave` for membership changes. |
| Team creation | No create action is currently available on the teams page. | `POST /api/teams` accepts `{ teamName }`. |
| Profiles | User profiles link to mock profile records. | No public-profile endpoint exists. Team member data currently includes only `username` and `email`. |

## API contracts and frontend types

### Teams

```ts
type TeamResponse<T> = {
  successful: boolean;
  message: string;
  data: T | null;
};
```

`GET /api/teams` returns a list of teams. Create, join, and leave success responses also return team lists. 
- Current Team objects expose `teamID`, `teamName`, `inviteCode`, `captain`, and ArrayList of `members`
- member objects expose `username` and `email`.

The frontend types mismatch: 
`TeamSummary` contains `id` and `name`, while `UserProfile` contains only `id` and `displayName`. The mock UI also uses fields the backend does not provide, including `description`, `hackathon`, `maxMembers`, and `lookingFor`. 

Membership requests are:

- `POST /api/teams/join`: `{ inviteCode, newMemberName }`
- `POST /api/teams/leave`: `{ teamName, userName }`

The team-detail UI assumes a user can belong to only one team. The backend does not currently enforce that rule or expose a current-user team lookup.

### Authentication
 Currently there are two /signin endpoints:
- `POST /auth/signin` accepts `{ username, password }` and returns `{ successful, message, data }`; `data` is currently `null` for both outcomes.
- `POST /signin` in /signupsigninendpoints is a hard-coded sample endpoint and is not the production auth contract.

The frontend currently signs in with `email` and signs up with `{ name, email, password }`. The backend authenticates by `username`, and `AuthService.signUp` accepts `{ username, email, password }`, but no `/auth/signup` controller route exists. The backend also does not yet return a user or session/token for the frontend to use.

### API client

`lib/api-client.ts` currently defines `API_BASE_URL` but does not make requests.

### Additional notes
User.java currently declare a Team field but currently unused.

