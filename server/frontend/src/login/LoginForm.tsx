import Button from "@mui/material/Button";
import {useNavigate} from "react-router-dom";
import {useAuth} from "./useAuth";

export default function LoginForm() {
    const navigate = useNavigate();

    const auth = useAuth();

    const login = () => {
        auth.login({
            id: 'id',
            name: 'My Name',
            email: 'my@email.com'
        }).then(() => {
            console.log("logged in")
            navigate("/");
        })
    };

    return <div>
        login form
        <div>Is Logged In: {JSON.stringify(!!auth.user)}</div>

        <div>
            <Button onClick={login} disabled={!!auth.user}>
                Login
            </Button>
        </div>
    </div>
}
