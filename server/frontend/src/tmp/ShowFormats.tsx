import React, {useEffect, useState} from "react";

export default function ShowFormats() {
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
        <h4>Formate</h4>
        <ul>
            {formats.map(f => {
                return <li key={f}>
                    {f}
                </li>
            })}
        </ul>
    </div>;
}
