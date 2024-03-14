import Button from "@mui/material/Button";
import {useContext} from "react";
import {LoginContext} from "./LoginContext";

export default function LoginForm() {
    const loginCtx = useContext(LoginContext);

    return <div>
        login form
        <div>Is Logged In: {JSON.stringify(loginCtx.isLoggedIn())}</div>

        <div>
            <Button onClick={() => loginCtx.login()}>
                Login
            </Button>
        </div>
    </div>
}
