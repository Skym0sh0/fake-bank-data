import Toolbar from "@mui/material/Toolbar";
import IconButton from "@mui/material/IconButton";
import {Paper} from "@mui/material";
import * as React from "react";
import {useState} from "react";
import Box from "@mui/material/Box";
import AppBar from "@mui/material/AppBar";
import MenuIcon from "@mui/icons-material/Menu";
import Typography from "@mui/material/Typography";
import Button from "@mui/material/Button";
import NavigationBar from "./NavigationBar";

export default function AppLayout() {
    const [open, setOpen] = useState(true);
    const toggleOpen = () => setOpen(prev => !prev);

    return (
        <Box sx={{display: 'flex', minHeight: "100vh", minWidth: "100vw"}}>
            <Paper sx={{height: "100vh"}} elevation={10}>
                <NavigationBar open={open} onClose={() => setOpen(false)}/>
            </Paper>
            <Box sx={{width: '100%', height: '100%'}}>
                <AppBar position="static">
                    <Toolbar>
                        <IconButton size="large"
                                    edge="start"
                                    color="inherit"
                                    aria-label="menu"
                                    sx={{mr: 2}}
                                    onClick={toggleOpen}>
                            <MenuIcon/>
                        </IconButton>

                        <Typography variant="h6" component="div" sx={{flexGrow: 1}}>
                            Regular Income
                        </Typography>

                        <Button color="inherit">
                            Login
                        </Button>
                    </Toolbar>
                </AppBar>
            </Box>
        </Box>
    );
}
