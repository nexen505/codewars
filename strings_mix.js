function getLettersMap(s1, s2) {
    const map = {};
    for (let i = 0; i < s1.length; i++) {
        if (s1.charAt(i).match(/[a-z]/i) && s1.charAt(i) === s1.charAt(i).toLowerCase()) {
            if (!map[s1.charAt(i)]) {
                map[s1.charAt(i)] = [1, 0];
            } else {
                map[s1.charAt(i)][0]++;
            }
        }
    }
    for (let i = 0; i < s2.length; i++) {
        if (s2.charAt(i).match(/[a-z]/i) && s2.charAt(i) === s2.charAt(i).toLowerCase()) {
            if (!map[s2.charAt(i)]) {
                map[s2.charAt(i)] = [0, 1];
            } else {
                map[s2.charAt(i)][1]++;
            }
        }
    }
    return map;
}

function mix(s1, s2) {
    const entries = [],
        map = getLettersMap(s1, s2);
    for (let key in map) {
        entries.push([key, map[key]]);
    }
    return entries.filter(([key, value]) => Math.max(...value) > 1)
        .map(([key, value]) => {
            const parts = [];
            let max = value[0];

            if (value[0] === value[1]) {
                parts[0] = "=";
            } else if (value[0] > value[1]) {
                parts[0] = "1";
            } else {
                parts[0] = "2";
                max = value[1];
            }
            parts[1]="";
            for (let i=1; i<=max; i++) parts[1]+=key;
            return parts.join(":");
        })
        .sort((part1, part2) => {
            let cmp = part2.length - part1.length;

            if (cmp != 0) return cmp;
            const parts1 = part1.split(":"),
                parts2 = part2.split(":");
            cmp = ((a, b) => {
                switch (a) {
                    case "1":
                        {
                            switch (b) {
                                case "1":
                                    return 0;
                                case "2":
                                case "=":
                                    return -1;
                            }
                        }
                    case "2":
                        {
                            switch (b) {
                                case "1":
                                    return 1;
                                case "2":
                                    return 0;
                                case "=":
                                    return -1;
                            }
                        }
                    case "=":
                        {
                            switch (b) {
                                case "1":
                                case "2":
                                    return 1;
                                case "=":
                                    return 0;
                            }
                        }
                }
                return 0;
            })(parts1[0], parts2[0]);
            if (cmp != 0) return cmp;
            return parts1[1].localeCompare(parts2[1]);
        })
        .join("/");
}
