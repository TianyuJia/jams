// formatDate takes a date string and returns a string in the format
// “yyyy-mm-dd”.
export function formatDate(value) {
	const date = new Date(value);

	const year = date.getFullYear();
	let month = date.getMonth();
	let day = date.getDate();

	if (month < 10) {
		month = "0" + month;
	}

	if (day < 10) {
		day = "0" + day;
	}

	return year + "-" + month + "-" + day;
}

export function formatDateTime(value) {
	return formatDate(value) + " " + formatTime(value);
}

// formatTime takes a date string and returns a string in the format
// “hh:mm:ss”.
export function formatTime(value) {
	const date = new Date(value);

	let hours = date.getHours();
	let minutes = date.getMinutes();
	let seconds = date.getSeconds();

	if (hours < 10) {
		hours = "0" + hours;
	}

	if (minutes < 10) {
		minutes = "0" + minutes;
	}

	if (seconds < 10) {
		seconds = "0" + seconds;
	}

	return hours + ":" + minutes + ":" + seconds;
}
