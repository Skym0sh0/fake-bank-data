import React, {useEffect, useState} from 'react';
import logo from './logo.svg';
import './App.css';

function App() {
    return (
        <div className="App">
            <header className="App-header">
                <img src={logo} className="App-logo" alt="logo"/>
                <p>
                    Edit <code>src/App.tsx</code> and save to reload.
                </p>
                <a
                    className="App-link"
                    href="https://reactjs.org"
                    target="_blank"
                    rel="noopener noreferrer"
                >
                    Learn React
                </a>
                Ist das so?

                <ShowFormats/>
            </header>
        </div>
    );
}

function ShowFormats() {
    const [isLoading, setIsLoading] = useState(false);

    const [formats, setFormats] = useState<string[]>([]);

    useEffect(() => {
        setIsLoading(true)

        setTimeout(() => {
            fetch("/api/turnover-import/formats", {
                headers: [
                    ['Authorization', 'Basic ' + window.btoa("default_user" + ":" + "password1234")],
                ],
            })
                // .then(response => {
                //     console.log("Regular Response", response)
                //     return response;
                // })
                .then((response => response.json()))
                .then(json => json as string[])
                .then(frmts => setFormats(frmts))
                .catch(error => console.error("Error", error))
                .finally(() => setIsLoading(false))
        }, 1500)
    }, []);

    if (isLoading)
        return <div>Loading ...</div>

    if (!formats.length)
        return <div>no formats</div>;

    return <div>
        <h6>Formate</h6>
        <ul>
            {formats.map(f => {
                return <li key={f}>
                    {f}
                </li>
            })}
        </ul>
    </div>;
}

export default App;
