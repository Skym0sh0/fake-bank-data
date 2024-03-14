import Button from "@mui/material/Button";
import {useContext} from "react";
import {LoginContext} from "./LoginContext";
import {useNavigate} from "react-router-dom";

export default function LoginForm() {
    const loginCtx = useContext(LoginContext);
    const navigate = useNavigate();

    const login = () => {
        loginCtx.login()
            .then(() => navigate("/"))
    };

    return <div>
        login form
        <div>Is Logged In: {JSON.stringify(loginCtx.isLoggedIn())}</div>

        <div>
            <Button onClick={login}>
                Login
            </Button>
        </div>
    </div>
}
