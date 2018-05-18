function checkWord(board, word) {
    const graph = board.map((row, rowNumber) => {
        return row.map((letter, position) => {
            return {
                name: letter,
                neighbours: getNeighbours(rowNumber, position, board),
                index: rowNumber * board.length + position
            };
        });
    });
    for (let i = 0; i < graph.length; i++) {
        for (let j = 0; j < graph[i].length; j++) {
            let point = graph[i][j];
            if (point.name !== word[0]) {
                continue;
            }
            if (searchNext(word, 1, point, graph, [])) {
                return true;
            }
        }
    }
    return false;
}

function searchNext(word, index, point, graph, visited) {
    if (word[index] == undefined) {
        return true;
    }
    const candidates = checkNeighbours(point, word[index], graph, visited);
    if (candidates.length === 0) {
        return false;
    }

    for (let i = 0; i < candidates.length; i++) {
        let candidate = candidates[i];
        if (index == word.length - 1) {
            return true;
        }
        let visited2 = visited.map(el => el);
        visited2.push(candidate.index);
        if (searchNext(word, index + 1, candidate, graph, visited2)) {
            return true;
        }
    }
    return false;
}

function checkNeighbours(point, letter, graph, visited) {
    let row;
    let index;
    return point.neighbours.filter(globalIndex => {
        row = Math.floor(globalIndex / graph.length);
        index = globalIndex % graph.length;
        if (!visited.includes(globalIndex) && letter === graph[row][index]['name']) {
            return true;
        }
    }).map(globalIndex => {
        row = Math.floor(globalIndex / graph.length);
        index = globalIndex % graph.length;
        return graph[row][index];
    });
}

function getNeighbours(row, index, board) {
    const neighbours = [];
    for (let i = row - 1; i <= row + 1; i++) {
        if (board[i] === undefined) {
            continue;
        }
        for (let j = index - 1; j <= index + 1; j++) {
            if ((i === row) && (j === index)) j++;
            if (board[i][j] !== undefined) {
                neighbours.push(i * board.length + j);
            }
        }
    }
    return neighbours;
}